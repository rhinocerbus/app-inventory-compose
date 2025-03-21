@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.LaunchedEffect
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
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.SearchCoordinator
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import kotlinx.coroutines.flow.StateFlow


class CreateItemStashSheetCoordinator(
	val stashesSourceFlow: StateFlow<ItemStashContentState>,
	val itemsSourceFlow: StateFlow<ItemContentState>,
	val locationsSourceFlow: StateFlow<LocationContentState>,
	val onAddItemToLocation: (slug: StashSlug) -> Unit,
	val onLaunchCreateItem: () -> Unit,
	val onLaunchCreateLocation: () -> Unit,
) : ModalSheetCoordinator() {
	val onShow: () -> Unit = _onShow
}

val stubCreateItemStashSheetCoordinator = CreateItemStashSheetCoordinator(
	stashesSourceFlow = previewItemStashesContentFlow(),
	itemsSourceFlow = previewItemsContentFlow(),
	locationsSourceFlow = previewLocationContentFlow(listOf(Location(id = "", createdAt = "", name = "Pantry"))),
	onAddItemToLocation = { },
	onLaunchCreateItem = { },
	onLaunchCreateLocation = { }
)

object CreateItemStashModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateItemStashSheetCoordinator,
	) {
		val itemsPool = coordinator.itemsSourceFlow.collectAsState().value
		val searchCoordinator = SearchCoordinator<Item>(
			onSearch = {
				val items = itemsPool.data.items
				if (it.isBlank()) {
					items
				} else {
					items.filter { item -> item.name.contains(it, true) }
				}
			}
		)
		//LaunchedEffect("create item stash init") {
		LaunchedEffect(itemsPool.hashCode()) {
			searchCoordinator.searchResultsState.value = coordinator.itemsSourceFlow.value.data.items
			searchCoordinator.onSearchUpdated(searchCoordinator.searchTermState.value)
		}

		var selectedLocations by remember { mutableStateOf(listOf<String>()) }

		val sheetState = rememberModalBottomSheetState(
			skipPartiallyExpanded = true
		)

		ModalBottomSheet(modifier = Modifier.fillMaxWidth(), onDismissRequest = {
			coordinator.onDismiss()
		}, sheetState = sheetState, dragHandle = { BottomSheetDefaults.DragHandle() }) {
			DrawContent(
				coordinator,
				// doesn't need to be hoisted, only used in scope of the modal
				searchCoordinator,
				selectedLocations,
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
		searchCoordinator: SearchCoordinator<Item>,
		selectedLocations: List<String>,
		onLocationToggle: (String, Boolean) -> Unit
	) {
		val locations = coordinator.locationsSourceFlow.collectAsState().value
		val stashes = coordinator.stashesSourceFlow.collectAsState().value

		// note - remembers are breaking things like newly-added items not appearing, dropdown being stuck after adding
		//val selectedItem by remember { searchCoordinator.selectedItem }
		//val searchActive by remember { searchCoordinator.searchActiveState }
		//val searchTerm by remember { searchCoordinator.searchTermState }
		//val searchResults by remember { searchCoordinator.searchResultsState }

		val selectedItem = searchCoordinator.selectedItem.value
		val searchActive = searchCoordinator.searchActiveState.value
		val searchTerm = searchCoordinator.searchTermState.value
		val searchResults = searchCoordinator.searchResultsState.value

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
						onExpandedChange = { searchCoordinator.onSearchActiveChanged(!searchActive) },
					) {
						TextField(
							modifier = Modifier
								.fillMaxWidth()
								.menuAnchor(MenuAnchorType.PrimaryEditable),
							value = selectedItem?.name ?: searchTerm,
							onValueChange = { searchCoordinator.onSearchUpdated(it) },
							label = { Text("Search for Item") },
							readOnly = selectedItem != null,
							trailingIcon = {
								if (selectedItem != null) {
									IconButton(
										onClick = {
											searchCoordinator.onSelectedItemChanged(null)
											searchCoordinator.onSearchUpdated("")
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
											coordinator.onLaunchCreateItem()
											searchCoordinator.onSearchActiveChanged(false)
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
								onDismissRequest = { searchCoordinator.onSearchActiveChanged(false) }
							) {
								searchResults.forEach { item ->
									DropdownMenuItem(
										onClick = {
											searchCoordinator.onSelectedItemChanged(item)
											searchCoordinator.onSearchUpdated(item.name)
											searchCoordinator.onSearchActiveChanged(false)
										},
										text = { Text(item.name) }
									)
								}
							}
						}
					}

					Gap(12.dp)

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
								coordinator.onAddItemToLocation(stashSlug)
							}
							coordinator.onDismiss
						}) {
						Icon(Icons.Default.Done, contentDescription = "add item to locations")
					}
				}

				Gap(12.dp)

				Text("Locations:")
				LazyColumn {
					itemsIndexed(items = locations.data.userLocations, key = { _, loc -> loc.id }) { _, loc ->
						val checked = selectedLocations.contains(loc.id)
						val prevAdded =
							stashes.data.itemStashes.firstOrNull { selectedItem != null && it.itemId == selectedItem?.id && it.locationId == loc.id } != null
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
						Surface(onClick = { coordinator.onLaunchCreateLocation() }) {
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
			stubCreateItemStashSheetCoordinator,
			SearchCoordinator<Item>(),
			selectedLocations = listOf(""),
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