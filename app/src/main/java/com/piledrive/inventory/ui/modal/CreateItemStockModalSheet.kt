@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.ui.callbacks.CreateItemCallbacks
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.inventory.ui.callbacks.stubCreateItemCallbacks
import com.piledrive.inventory.ui.forms.state.TextFormFieldState
import com.piledrive.inventory.ui.forms.validators.Validators
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.theme.AppTheme
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import kotlinx.coroutines.flow.StateFlow


class CreateItemStockSheetCoordinator(
	val showSheetState: MutableState<Boolean> = mutableStateOf(false),
	val createItemCallbacks: CreateItemCallbacks = stubCreateItemCallbacks,
	val modalSheetCallbacks: ModalSheetCallbacks = object : ModalSheetCallbacks {
		override val onDismissed: () -> Unit = {
			showSheetState.value = false
		}
	}
)

object CreateItemStockModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateItemStockSheetCoordinator,
		itemSheetCoordinator: CreateItemSheetCoordinator,
		locationSheetCoordinator: CreateLocationModalSheetCoordinator,
		itemState: StateFlow<ItemContentState>,
		locationsContentState: StateFlow<LocationContentState>
	) {
		var selectedItem: Item? by remember { mutableStateOf(null) }
		var searchTerm: String by remember { mutableStateOf("") }
		var searchActive: Boolean by remember { mutableStateOf(false) }

		var selectedLocations by remember { mutableStateOf(listOf<String>()) }
		/*
			or
			val selectedTags = remember { mutableStateListOf<String>() }
		 */

		val sheetState = rememberModalBottomSheetState(
			skipPartiallyExpanded = true
		)
		ModalBottomSheet(modifier = Modifier.fillMaxWidth(), onDismissRequest = {
			coordinator.modalSheetCallbacks.onDismissed()
		}, sheetState = sheetState, dragHandle = { BottomSheetDefaults.DragHandle() }) {
			DrawContent(
				coordinator,
				itemSheetCoordinator,
				locationSheetCoordinator,
				locationsContentState,
				selectedItem,
				onSelectedItemChanged = {
					selectedItem = it
				},
				selectedLocations,
				searchActive,
				onSearchActiveChanged = {
					searchActive = it
				},
				searchTerm,
				onSearchUpdated = {
					searchTerm = it
				},
				onTagToggle = { id, update ->
					selectedLocations = if (update) {
						selectedLocations + id
					} else {
						selectedLocations - id
					}
				})
		}
	}

	@Composable
	internal fun DrawContent(
		coordinator: CreateItemStockSheetCoordinator,
		itemSheetCoordinator: CreateItemSheetCoordinator,
		locationSheetCoordinator: CreateLocationModalSheetCoordinator,
		locationsContentState: StateFlow<LocationContentState>,
		selectedItem: Item?,
		onSelectedItemChanged: (Item?) -> Unit,
		selectedLocations: List<String>,
		searchActive: Boolean,
		onSearchActiveChanged: (Boolean) -> Unit,
		searchTerm: String,
		onSearchUpdated: (String) -> Unit,
		onTagToggle: (String, Boolean) -> Unit
	) {
		val locations = locationsContentState.collectAsState().value

		Surface(
			modifier = Modifier.fillMaxWidth()
		) {
			val formState = remember {
				TextFormFieldState(
					mainValidator = Validators.Required(errMsg = "Item name required")
				)
			}

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 12.dp),
			) {
				Text("Item:")
				Row(
					modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
				) {
					SearchBar(modifier = Modifier.weight(1f), inputField = {
						Row {
							if (selectedItem != null) {
								InputChip(
									label = { Text(selectedItem.name) },
									onClick = { onSelectedItemChanged(null) },
									trailingIcon = { Icon(Icons.Default.Clear, "clear item") },
									selected = false
								)
							}
							TextField(modifier = Modifier,
								value = searchTerm,
								label = { Text("Search for Item") },
								onValueChange = { onSearchUpdated(it) })
						}
					}, onExpandedChange = {}, expanded = false
					) {

					}

					Spacer(Modifier.size(12.dp))

					IconButton(modifier = Modifier.size(40.dp),
						enabled = formState.isValid,
						onClick = {
							itemSheetCoordinator.showSheetState.value = true
						}) {
						Icon(Icons.Default.Add, contentDescription = "add new item")
					}
				}

				Spacer(Modifier.size(12.dp))

				Text("Locations:")
				LazyColumn {
					itemsIndexed(items = locations.data.userLocations, key = { _, loc -> loc.id }) { _, loc ->
						val checked = selectedLocations.contains(loc.id)
						Row() {
							Checkbox(
								checked = checked,
								onCheckedChange = {onTagToggle(loc.id, !checked)}
							)
							Text(loc.name)
						}
					}
					item {
						Surface(onClick = { locationSheetCoordinator.showSheetState.value = true }) {
							Row {
								Icon(Icons.Default.Add, "Add new location")
								Text("Add new location")
							}
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun CreateItemStockSheetPreview() {
	AppTheme {
		CreateItemStockModalSheet.DrawContent(
			CreateItemStockSheetCoordinator(),
			CreateItemSheetCoordinator(),
			CreateLocationModalSheetCoordinator(),
			previewLocationContentFlow(listOf(Location(id = "", createdAt = "", name = "Pantry"))),
			Item(id = "", createdAt = "", name = "Cheese", tags = listOf(), unit = QuantityUnit.defaultUnitBags),
			onSelectedItemChanged = {},
			selectedLocations = listOf(""),
			false,
			onSearchActiveChanged = {},
			searchTerm = "",
			onSearchUpdated = {},
			onTagToggle = { _, _ -> })
	}
}