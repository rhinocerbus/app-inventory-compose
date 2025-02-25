@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.ui.callbacks.CreateLocationCallbacks
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.inventory.ui.callbacks.stubCreateLocationCallbacks
import com.piledrive.inventory.ui.callbacks.stubModalSheetCallbacks
import com.piledrive.inventory.ui.forms.state.TextFormFieldState
import com.piledrive.inventory.ui.forms.validators.Validators
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewMainContentFlow
import com.piledrive.inventory.viewmodel.SampleViewModel
import kotlinx.coroutines.flow.StateFlow

object MainScreen : NavRoute {
	override val routeValue: String = "home"

	@Composable
	fun draw(
		viewModel: SampleViewModel,
	) {
		var showCreateLocationBottomSheet by remember { mutableStateOf(false) }
		val createLocationCallbacks = object : CreateLocationCallbacks {
			override val onShowCreate: () -> Unit = {
				showCreateLocationBottomSheet = true
			}
			override val onAddLocation: (name: String) -> Unit = {
				viewModel.addNewLocation(it)
			}
		}

		val modalSheetCallbacks = object : ModalSheetCallbacks {
			override val onDismissed: () -> Unit = { showCreateLocationBottomSheet = false }
		}

		drawContent(
			viewModel.userLocationContentState,
			showCreateLocationBottomSheet,
			createLocationCallbacks,
			modalSheetCallbacks
		)
	}

	@Composable
	fun drawContent(
		contentState: StateFlow<LocationContentState>,
		showCreateLocationBottomSheet: Boolean,
		createLocationCallbacks: CreateLocationCallbacks,
		modalSheetCallbacks: ModalSheetCallbacks,
	) {
		val content = contentState.collectAsState().value
		Scaffold(
			topBar = {
				DrawBarWithFilters()
			},
			content = { innerPadding ->
				Column(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize(),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					when {
						content.data.userLocations.isEmpty() -> {
							if (content.hasLoaded) {
								DrawEmptyLocationsState(createLocationCallbacks)
							} else {
								// main spinner
							}
						}

						else -> {
							// content

							if (content.isLoading) {
								// secondary spinner?
							}
						}
					}
					if (content.data.userLocations.isEmpty())
						if (showCreateLocationBottomSheet) {
							DrawCreateLocationSheet(Modifier, modalSheetCallbacks)
						}
				}
			}
		)
	}

	@Composable
	fun DrawBarWithFilters(modifier: Modifier = Modifier) {
		var showLocations by remember { mutableStateOf(false) }
		var showTags by remember { mutableStateOf(false) }
		TopAppBar(
			title = {
				Text("What's in the: ")
			},
			actions = {
				Button(onClick = { showLocations = true }) {
					Text("Loc")
				}
				DropdownMenu(
					expanded = showLocations,
					onDismissRequest = { showLocations = false }
				) {
					DropdownMenuItem(
						text = { Text("Loc A") }, onClick = { showLocations = false }
					)
					DropdownMenuItem(
						text = { Text("Loc B") }, onClick = { showLocations = false }
					)
					DropdownMenuItem(
						text = { Text("Loc V") }, onClick = { showLocations = false }
					)
					DropdownMenuItem(
						text = { Text("Loc F") }, onClick = { showLocations = false }
					)
				}

				Button(onClick = { showTags = true }) {
					Text("Tag")
				}
				DropdownMenu(
					expanded = showTags,
					onDismissRequest = { showTags = false }
				) {
					DropdownMenuItem(
						text = { Text("Tag A") }, onClick = { showTags = false }
					)
					DropdownMenuItem(
						text = { Text("Tag B") }, onClick = { showTags = false }
					)
					DropdownMenuItem(
						text = { Text("Tag V") }, onClick = { showTags = false }
					)
					DropdownMenuItem(
						text = { Text("Tag F") }, onClick = { showTags = false }
					)
				}

			}
		)
	}

	@Composable
	fun DrawLocationItems(modifier: Modifier = Modifier) {
		LazyColumn() { }
	}

	@Composable
	fun DrawEmptyLocationsState(createLocationCallbacks: CreateLocationCallbacks) {
		// empty
		Text(
			"no locations :("
		)
		Button(onClick = {
			createLocationCallbacks.onShowCreate()
		}) {
			Text("add location")
		}
	}

	@Composable
	fun DrawCreateLocationSheet(modifier: Modifier = Modifier, modalSheetCallbacks: ModalSheetCallbacks) {
		val sheetState = rememberModalBottomSheetState(
			skipPartiallyExpanded = true
		)
		ModalBottomSheet(
			onDismissRequest = {
				modalSheetCallbacks.onDismissed()
			},
			sheetState = sheetState,
			dragHandle = { BottomSheetDefaults.DragHandle() }
		) {
			Surface(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 12.dp)
			) {

				val formState = remember {
					TextFormFieldState(
						mainValidator = Validators.Required(errMsg = "Location name required")
					)
				}

				OutlinedTextField(
					value = formState.currentValue ?: "",
					isError = formState.hasError,
					supportingText = {
						if (formState.hasError) {
							Text(
								modifier = Modifier.fillMaxWidth(),
								text = formState.errorMsg ?: "",
								color = MaterialTheme.colorScheme.error
							)
						}
					},
					label = { Text("Location name") },
					onValueChange = { formState.check(it) }
				)
			}
		}
	}
}

@Preview
@Composable
fun MainPreview() {
	val contentState = previewMainContentFlow()
	MainScreen.drawContent(
		contentState,
		false,
		stubCreateLocationCallbacks,
		stubModalSheetCallbacks
	)
}