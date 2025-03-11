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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
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
import com.piledrive.inventory.data.model.StashSlug
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.theme.AppTheme
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import kotlinx.coroutines.flow.StateFlow


interface AddItemStashCallbacks {
	//val onShowAdd: (startingLocation: Location?) -> Unit
	val onAddItemToLocation: (slug: StashSlug) -> Unit
}

val stubAddItemStashCallbacks = object : AddItemStashCallbacks {
	//override val onShowAdd: (startingLocation: Location?) -> Unit = {}
	override val onAddItemToLocation: (slug: StashSlug) -> Unit = { }
}

class CreateItemStashSheetCoordinator(
	val showSheetState: MutableState<Boolean> = mutableStateOf(false),
	val createItemStashCallbacks: AddItemStashCallbacks = stubAddItemStashCallbacks,
	val modalSheetCallbacks: ModalSheetCallbacks = object : ModalSheetCallbacks {
		override val onDismissed: () -> Unit = {
			showSheetState.value = false
		}
	}
)

object CreateItemStashModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateItemStashSheetCoordinator,
		itemSheetCoordinator: CreateItemSheetCoordinator,
		locationSheetCoordinator: CreateLocationModalSheetCoordinator,
		stashesState: StateFlow<ItemStashContentState>,
		itemState: StateFlow<ItemContentState>,
		locationsContentState: StateFlow<LocationContentState>,
	) {
		val itemsPool = itemState.collectAsState().value

		var selectedItem: Item? by remember { mutableStateOf(null) }
		var searchTerm: String by remember { mutableStateOf("") }
		var searchActive: Boolean by remember { mutableStateOf(false) }
		var searchResults: List<Item> by remember { mutableStateOf(itemsPool.data.items) }

		var selectedLocations by remember { mutableStateOf(listOf<String>()) }

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
				stashesState,
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
					val items = itemsPool.data.items
					val updatedResults = if (it.isBlank()) {
						items
					} else {
						items.filter { it.name.contains(searchTerm, true) }
					}
					searchResults = updatedResults
				},
				searchResults,
				onLocationToggle = { id, update ->
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
		coordinator: CreateItemStashSheetCoordinator,
		itemSheetCoordinator: CreateItemSheetCoordinator,
		locationSheetCoordinator: CreateLocationModalSheetCoordinator,
		stashesState: StateFlow<ItemStashContentState>,
		locationsContentState: StateFlow<LocationContentState>,
		selectedItem: Item?,
		onSelectedItemChanged: (Item?) -> Unit,
		selectedLocations: List<String>,
		searchActive: Boolean,
		onSearchActiveChanged: (Boolean) -> Unit,
		searchTerm: String,
		onSearchUpdated: (String) -> Unit,
		searchResults: List<Item>,
		onLocationToggle: (String, Boolean) -> Unit
	) {
		val locations = locationsContentState.collectAsState().value
		val stashes = stashesState.collectAsState().value

		Surface(
			modifier = Modifier.fillMaxWidth()
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(12.dp),
			) {
				Row(
					modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
				) {

					ExposedDropdownMenuBox(
						modifier = Modifier.weight(1f),
						expanded = searchActive,
						onExpandedChange = { onSearchActiveChanged(!searchActive) },
					) {
						TextField(
							modifier = Modifier
								.fillMaxWidth()
								.menuAnchor(MenuAnchorType.PrimaryEditable),
							value = selectedItem?.name ?: searchTerm,
							onValueChange = { onSearchUpdated(it) },
							label = { Text("Search for Item") },
							readOnly = selectedItem != null,
							trailingIcon = {
								if (selectedItem != null) {
									IconButton(
										onClick = {
											onSelectedItemChanged(null)
											onSearchUpdated("")
										}
									) {
										Icon(
											Icons.Default.Clear,
											"clear item",
										)
									}
								} else {
									IconButton(
										onClick = {
											itemSheetCoordinator.showSheetState.value = true
										}
									) {
										Icon(Icons.Default.Add, contentDescription = "add new item")
									}
								}
							}
						)
						if (selectedItem == null) {
							ExposedDropdownMenu(
								expanded = searchActive,
								onDismissRequest = { onSearchActiveChanged(false) }
							) {
								searchResults.forEach { item ->
									DropdownMenuItem(
										onClick = {
											onSelectedItemChanged(item)
											onSearchUpdated(item.name)
											onSearchActiveChanged(false)
										},
										text = { Text(item.name) }
									)
								}
							}
						}
					}

					Spacer(Modifier.size(12.dp))

					IconButton(modifier = Modifier.size(40.dp),
						enabled = selectedItem != null,
						onClick = {
							selectedLocations.forEach { loc ->
								/* todo
										- add another callback layer to have viewmodel do content-level validation (dupe check)
										- dismiss based on success of ^
										- also have error message from ^
										requires fleshing out and/or moving form state to viewmodel, can't decide if better left internal or add
										form-level viewmodel, feels like clutter in the main VM
								 */
								val stashSlug = StashSlug(selectedItem!!.id, loc, 0.0)
								coordinator.createItemStashCallbacks.onAddItemToLocation(stashSlug)
							}
						}) {
						Icon(Icons.Default.Done, contentDescription = "add item to locations")
					}
				}

				Spacer(Modifier.size(12.dp))

				Text("Locations:")
				LazyColumn {
					itemsIndexed(items = locations.data.userLocations, key = { _, loc -> loc.id }) { _, loc ->
						val checked = selectedLocations.contains(loc.id)
						val prevAdded =
							stashes.data.itemStashes.firstOrNull { selectedItem != null && it.itemId == selectedItem.id && it.locationId == loc.id } != null
						Row(verticalAlignment = Alignment.CenterVertically) {
							Checkbox(
								enabled = !prevAdded && selectedItem != null,
								checked = checked || prevAdded,
								onCheckedChange = { onLocationToggle(loc.id, !checked) }
							)
							Text(loc.name)
						}
					}
					item {
						Surface(onClick = { locationSheetCoordinator.showSheetState.value = true }) {
							Row(verticalAlignment = Alignment.CenterVertically) {
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
private fun CreateItemStashSheetPreview() {
	AppTheme {
		CreateItemStashModalSheet.DrawContent(
			CreateItemStashSheetCoordinator(),
			CreateItemSheetCoordinator(),
			CreateLocationModalSheetCoordinator(),
			previewItemStashesContentFlow(),
			previewLocationContentFlow(listOf(Location(id = "", createdAt = "", name = "Pantry"))),
			selectedItem = null,
			onSelectedItemChanged = {},
			selectedLocations = listOf(""),
			false,
			onSearchActiveChanged = {},
			searchTerm = "",
			onSearchUpdated = {},
			listOf(),
			onLocationToggle = { _, _ -> })
	}
}


///////////// scrap


/*
					DockedSearchBar(
						modifier = Modifier,
						inputField = {
							Row {
								if (selectedItem != null) {
									InputChip(
										label = { Text(selectedItem.name) },
										onClick = { onSelectedItemChanged(null) },
										trailingIcon = { Icon(Icons.Default.Clear, "clear item") },
										selected = false
									)
								}
								SearchBarDefaults.InputField(
									modifier = Modifier,
									query = searchTerm,
									onQueryChange = { onSearchUpdated(it) },
									onSearch = { onSearchUpdated(it) },
									placeholder = { Text("Search for Item") },
									expanded = searchActive,
									onExpandedChange = {
										onSearchActiveChanged(it)
									},
								)
							}
						},
						onExpandedChange = {
							onSearchActiveChanged(it)
						},
						expanded = searchActive
					) {
						LazyColumn(modifier = Modifier) {
							itemsIndexed(searchResults, key = { _, item -> item.id }) { _, item ->
								Surface(
									onClick = {
										onSelectedItemChanged(item)
									}
								) {
									Text(item.name)
								}
							}
						}
					}
*/