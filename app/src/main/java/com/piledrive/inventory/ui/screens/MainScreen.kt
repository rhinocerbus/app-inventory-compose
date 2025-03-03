@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.callbacks.ContentFilterCallbacks
import com.piledrive.inventory.ui.callbacks.CreateLocationCallbacks
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.inventory.ui.callbacks.stubContentFilterCallbacks
import com.piledrive.inventory.ui.callbacks.stubCreateLocationCallbacks
import com.piledrive.inventory.ui.callbacks.stubModalSheetCallbacks
import com.piledrive.inventory.ui.modal.CreateLocationModalSheet
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewMainContentFlow
import com.piledrive.inventory.viewmodel.LocationsListsViewModel
import kotlinx.coroutines.flow.StateFlow

object MainScreen : NavRoute {
	override val routeValue: String = "home"

	@Composable
	fun draw(
		viewModel: LocationsListsViewModel,
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

		val contentFilterCallbacks = object : ContentFilterCallbacks {
			override val onLocationChanged: (loc: Location) -> Unit = {
				viewModel.changeLocation(it)
			}
			override val onTagChanged: (tag: Tag) -> Unit = {}
		}

		drawContent(
			viewModel.userLocationContentState,
			showCreateLocationBottomSheet,
			createLocationCallbacks,
			modalSheetCallbacks,
			contentFilterCallbacks
		)
	}

	@Composable
	fun drawContent(
		contentState: StateFlow<LocationContentState>,
		showCreateLocationBottomSheet: Boolean,
		createLocationCallbacks: CreateLocationCallbacks,
		modalSheetCallbacks: ModalSheetCallbacks,
		contentFilterCallbacks: ContentFilterCallbacks
	) {
		Scaffold(
			topBar = {
				DrawBarWithFilters(Modifier, contentState, contentFilterCallbacks)
			},
			content = { innerPadding ->
				DrawBody(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize(),
					contentState = contentState,
					showCreateLocationBottomSheet,
					createLocationCallbacks,
					modalSheetCallbacks
				)
			}
		)
	}

	@Composable
	fun DrawBody(
		modifier: Modifier = Modifier,
		contentState: StateFlow<LocationContentState>,
		showCreateLocationBottomSheet: Boolean,
		createLocationCallbacks: CreateLocationCallbacks,
		modalSheetCallbacks: ModalSheetCallbacks,
	) {
		val content = contentState.collectAsState().value
		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			when {
				content.data.userLocations.isEmpty() -> {
					if (content.hasLoaded) {
						// empty
						DrawEmptyLocationsState(createLocationCallbacks)
					} else {
						// main spinner
					}
				}

				else -> {
					// content
					LazyColumn(
						modifier = Modifier.fillMaxSize(),
					) {
						//items
						//itemsIndexed(content.data.)
					}

					if (content.isLoading) {
						// secondary spinner?
					}
				}
			}

			if (showCreateLocationBottomSheet) {
				CreateLocationModalSheet.Draw(Modifier, modalSheetCallbacks, createLocationCallbacks)
			}
		}
	}

	@Composable
	fun DrawBarWithFilters(modifier: Modifier = Modifier, contentState: StateFlow<LocationContentState>, callbacks: ContentFilterCallbacks) {
		val content = contentState.collectAsState().value

		var showLocations by remember { mutableStateOf(false) }
		var showTags by remember { mutableStateOf(false) }
		TopAppBar(
			title = {
				Text("What's in the: ")
			},
			actions = {
				Button(onClick = { showLocations = true }) {
					Text(content.data.currentLocation.name)
				}
				DropdownMenu(
					expanded = showLocations,
					onDismissRequest = { showLocations = false }
				) {
					content.data.allLocations.forEach {
						DropdownMenuItem(
							text = { Text(it.name) }, onClick = {
								callbacks.onLocationChanged(it)
								showLocations = false
							}
						)
					}
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
	fun ColumnScope.DrawLocationItems(modifier: Modifier = Modifier) {
		LazyColumn() { }
	}

	@Composable
	fun ColumnScope.DrawEmptyLocationsState(createLocationCallbacks: CreateLocationCallbacks) {
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
}

@Preview
@Composable
fun MainPreview() {
	val contentState = previewMainContentFlow()
	MainScreen.drawContent(
		contentState,
		false,
		stubCreateLocationCallbacks,
		stubModalSheetCallbacks,
		stubContentFilterCallbacks
	)
}