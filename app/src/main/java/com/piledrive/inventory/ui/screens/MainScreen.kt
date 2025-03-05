@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.Stock
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.callbacks.AddItemStockCallbacks
import com.piledrive.inventory.ui.callbacks.ContentFilterCallbacks
import com.piledrive.inventory.ui.callbacks.CreateItemCallbacks
import com.piledrive.inventory.ui.callbacks.CreateLocationCallbacks
import com.piledrive.inventory.ui.callbacks.CreateTagCallbacks
import com.piledrive.inventory.ui.callbacks.stubAddItemStockCallbacks
import com.piledrive.inventory.ui.callbacks.stubContentFilterCallbacks
import com.piledrive.inventory.ui.modal.CreateItemModalSheet
import com.piledrive.inventory.ui.modal.CreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.CreateLocationModalSheet
import com.piledrive.inventory.ui.modal.CreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.modal.CreateTagModalSheet
import com.piledrive.inventory.ui.modal.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStockContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.inventory.ui.util.previewItemStocksContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.viewmodel.MainViewModel
import kotlinx.coroutines.flow.StateFlow

object MainScreen : NavRoute {
	override val routeValue: String = "home"

	@Composable
	fun draw(
		viewModel: MainViewModel,
	) {
		val createLocationCoordinator = CreateLocationModalSheetCoordinator(
			createLocationCallbacks = object : CreateLocationCallbacks {
				override val onAddLocation: (name: String) -> Unit = {
					viewModel.addNewLocation(it)
				}
			}
		)

		val createTagCoordinator = CreateTagSheetCoordinator(
			createTagCallbacks = object : CreateTagCallbacks {
				override val onAddTag: (name: String) -> Unit = {
					viewModel.addNewTag(it)
				}
			}
		)

		val createItemCoordinator = CreateItemSheetCoordinator(
			createItemCallbacks = object : CreateItemCallbacks {
				override val onAddItem: (item: ItemSlug) -> Unit = {
					viewModel.addNewItem(it)
				}
			}
		)

		val addItemStockCallbacks = object : AddItemStockCallbacks {
			override val onShowAdd: (startingLocation: Location?) -> Unit = {}
			override val onAddItemToLocation: (itemStock: Stock, location: Location) -> Unit = { _, _ -> }
		}


		val contentFilterCallbacks = object : ContentFilterCallbacks {
			override val onLocationChanged: (loc: Location) -> Unit = {
				viewModel.changeLocation(it)
			}
			override val onTagChanged: (tag: Tag) -> Unit = {}
		}

		drawContent(
			viewModel.userLocationContentState,
			viewModel.userTagsContentState,
			viewModel.itemsContentState,
			viewModel.itemStocksContentState,
			createLocationCoordinator,
			createTagCoordinator,
			createItemCoordinator,
			addItemStockCallbacks,
			contentFilterCallbacks
		)
	}

	@Composable
	fun drawContent(
		locationState: StateFlow<LocationContentState>,
		tagState: StateFlow<TagsContentState>,
		itemState: StateFlow<ItemContentState>,
		itemStockState: StateFlow<ItemStockContentState>,
		createLocationCoordinator: CreateLocationModalSheetCoordinator,
		createTagCoordinator: CreateTagSheetCoordinator,
		createItemCoordinator: CreateItemSheetCoordinator,
		addItemStockCallbacks: AddItemStockCallbacks,
		contentFilterCallbacks: ContentFilterCallbacks
	) {
		Scaffold(
			topBar = {
				DrawBarWithFilters(Modifier, locationState, tagState, contentFilterCallbacks)
			},
			content = { innerPadding ->
				DrawBody(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize(),
					locationState,
					tagState,
					itemState,
					itemStockState,
					createLocationCoordinator,
					createTagCoordinator,
					createItemCoordinator,
					addItemStockCallbacks,
				)
			},
			floatingActionButton = {
				DrawAddContentFab(Modifier, createLocationCoordinator, createTagCoordinator, createItemCoordinator)
			},
		)
	}

	@Composable
	fun DrawBody(
		modifier: Modifier = Modifier,
		locationState: StateFlow<LocationContentState>,
		tagState: StateFlow<TagsContentState>,
		itemState: StateFlow<ItemContentState>,
		itemStockState: StateFlow<ItemStockContentState>,
		createLocationCoordinator: CreateLocationModalSheetCoordinator,
		createTagCoordinator: CreateTagSheetCoordinator,
		createItemCoordinator: CreateItemSheetCoordinator,
		addItemStockCallbacks: AddItemStockCallbacks,
	) {
		val showLocationSheet: Boolean by remember { createLocationCoordinator.showSheetState }
		val showTagSheet: Boolean by remember { createTagCoordinator.showSheetState }
		val showItemSheet: Boolean by remember { createItemCoordinator.showSheetState }

		val locationContent = locationState.collectAsState().value
		val itemStockContent = itemStockState.collectAsState().value

		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			when {
				locationContent.data.userLocations.isEmpty() -> {
					if (locationContent.hasLoaded) {
						// empty
						DrawEmptyLocationsState(createLocationCoordinator)
					} else {
						// main spinner
					}
				}

				itemStockContent.data.itemStocks.isEmpty() -> {
					if (locationContent.data.currentLocation.id == STATIC_ID_LOCATION_ALL) {
						Text(
							"no items anywhere"
						)
						Button(onClick = {
							createLocationCoordinator.showSheetState.value = true
						}) {
							Text("add item")
						}
					} else {
						Text(
							"no items in ${locationContent.data.currentLocation.name}"
						)
						Button(onClick = {
							addItemStockCallbacks.onShowAdd(locationContent.data.currentLocation)
						}) {
							Text("add item")
						}
					}
				}

				else -> {
					// content
					LazyColumn(
						modifier = Modifier.fillMaxSize(),
					) {
						itemsIndexed(
							itemStockContent.data.itemStocks,
							key = { _, item ->
								item.id
							}
						) { _, item ->
							Text(item.id)
						}
					}

					if (locationContent.isLoading) {
						// secondary spinner?
					}
				}
			}

			if (showLocationSheet) {
				CreateLocationModalSheet.Draw(Modifier, createLocationCoordinator)
			}

			if(showItemSheet) {
				CreateItemModalSheet.Draw(Modifier, createItemCoordinator, createTagCoordinator, itemState, tagState)
			}

			if (showTagSheet) {
				CreateTagModalSheet.Draw(Modifier, createTagCoordinator, tagState)
			}
		}
	}

	@Composable
	fun DrawAddContentFab(
		modifier: Modifier = Modifier,
		createLocationCoordinator: CreateLocationModalSheetCoordinator,
		createTagCoordinator: CreateTagSheetCoordinator,
		createItemCoordinator: CreateItemSheetCoordinator
	) {
		/*
			box added to satisfy dropdown requirement for a sibling wrapped in a parent to anchor
			https://stackoverflow.com/a/66807367
		 */
		Box {
			var showMenu by remember { mutableStateOf(false) }
			FloatingActionButton(
				onClick = {
					showMenu = true
				}
			) {
				Icon(Icons.Default.Add, "Show 'add content' menu")
			}
			DropdownMenu(
				expanded = showMenu,
				onDismissRequest = { showMenu = false }
			) {
				DropdownMenuItem(
					text = { Text("Add item") }, onClick = {
						createItemCoordinator.showSheetState.value = true
						showMenu = false
					}
				)
				DropdownMenuItem(
					text = { Text("Add location") }, onClick = {
						createLocationCoordinator.showSheetState.value = true
						showMenu = false
					}
				)
				DropdownMenuItem(
					text = { Text("Add tag") }, onClick = {
						createTagCoordinator.showSheetState.value = true
						showMenu = false
					}
				)
			}
		}
	}

	@Composable
	fun DrawBarWithFilters(
		modifier: Modifier = Modifier,
		locationState: StateFlow<LocationContentState>,
		tagState: StateFlow<TagsContentState>,
		callbacks: ContentFilterCallbacks
	) {
		TopAppBar(
			title = {
				Text("What's in the: ")
			},
			actions = {
				DrawLocationsOptions(locationState = locationState, callbacks = callbacks)
				DrawTagOptions(tagState = tagState, callbacks = callbacks)
			}
		)
	}

	@Composable
	fun DrawLocationsOptions(
		modifier: Modifier = Modifier,
		locationState: StateFlow<LocationContentState>,
		callbacks: ContentFilterCallbacks
	) {
		val locationsContent = locationState.collectAsState().value
		var showLocations by remember { mutableStateOf(false) }
		Button(onClick = { showLocations = true }) {
			Text(locationsContent.data.currentLocation.name)
		}
		DropdownMenu(
			expanded = showLocations,
			onDismissRequest = { showLocations = false }
		) {
			locationsContent.data.allLocations.forEach {
				DropdownMenuItem(
					text = { Text(it.name) }, onClick = {
						callbacks.onLocationChanged(it)
						showLocations = false
					}
				)
			}
		}
	}

	@Composable
	fun DrawTagOptions(
		modifier: Modifier = Modifier,
		tagState: StateFlow<TagsContentState>,
		callbacks: ContentFilterCallbacks
	) {
		val tagsContent = tagState.collectAsState().value
		var showTags by remember { mutableStateOf(false) }
		Button(onClick = { showTags = true }) {
			Text(tagsContent.data.currentTag.name)
		}
		DropdownMenu(
			expanded = showTags,
			onDismissRequest = { showTags = false }
		) {
			tagsContent.data.allTags.forEach {
				DropdownMenuItem(
					text = { Text(it.name) }, onClick = {
						callbacks.onTagChanged(it)
						showTags = false
					}
				)
			}
		}
	}

	@Composable
	fun ColumnScope.DrawLocationItems(modifier: Modifier = Modifier) {
		LazyColumn() { }
	}

	@Composable
	fun ColumnScope.DrawEmptyLocationsState(
		createLocationCoordinator: CreateLocationModalSheetCoordinator,
	) {
		// empty
		Text(
			"no locations :("
		)
		Button(onClick = {
			createLocationCoordinator.showSheetState.value = true
		}) {
			Text("add location")
		}
	}
}

@Preview
@Composable
fun MainPreview() {
	MainScreen.drawContent(
		previewLocationContentFlow(),
		previewTagsContentFlow(),
		previewItemsContentFlow(),
		previewItemStocksContentFlow(),
		CreateLocationModalSheetCoordinator(),
		CreateTagSheetCoordinator(),
		CreateItemSheetCoordinator(),
		stubAddItemStockCallbacks,
		stubContentFilterCallbacks
	)
}