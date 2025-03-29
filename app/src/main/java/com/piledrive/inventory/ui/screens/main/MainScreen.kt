@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.callbacks.ContentFilterCallbacks
import com.piledrive.inventory.ui.callbacks.stubContentFilterCallbacks
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitModalSheet
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.CreateTagModalSheet
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_item.CreateItemModalSheet
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.stubCreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_item_stash.CreateItemStashModalSheet
import com.piledrive.inventory.ui.modal.create_item_stash.CreateItemStashSheetCoordinator
import com.piledrive.inventory.ui.modal.create_item_stash.CreateItemStashSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item_stash.stubCreateItemStashSheetCoordinator
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheet
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_location.stubCreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.stubCreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.stubCreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.modal.transfer_item.TransferItemStashModalSheet
import com.piledrive.inventory.ui.modal.transfer_item.TransferItemStashSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.transfer_item.stubTransferItemStashSheetCoordinator
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.screens.main.content.MainContentListCoordinator
import com.piledrive.inventory.ui.screens.main.content.MainStashContentList
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewQuantityUnitsContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.inventory.viewmodel.MainViewModel
import kotlinx.coroutines.flow.StateFlow

object MainScreen : NavRoute {
	override val routeValue: String = "home"

	@Composable
	fun draw(
		viewModel: MainViewModel,
	) {
		val createLocationCoordinator = CreateLocationModalSheetCoordinator(
			onAddLocation = {
				viewModel.addNewLocation(it)
			}
		)

		val createTagCoordinator = CreateTagSheetCoordinator(
			onAddTag = {
				viewModel.addNewTag(it)
			}
		)

		val createItemCoordinator = CreateItemSheetCoordinator(
			onAddItem = { viewModel.addNewItem(it) }
		)

		val createItemStashCoordinator = CreateItemStashSheetCoordinator(
			viewModel.itemStashesContentState,
			viewModel.itemsContentState,
			viewModel.userLocationContentState,
			onAddItemToLocation = {
				viewModel.addNewItemStash(it)
			},
			onLaunchCreateItem = {
				createItemCoordinator.showSheet()
			},
			onLaunchCreateLocation = {
				createLocationCoordinator.showSheet()
			}
		)

		val createQuantityUnitSheetCoordinator = CreateQuantityUnitSheetCoordinator(
			onAddQuantityUnit = {
				viewModel.addNewQuantityUnit(it)
			}
		)

		val contentFilterCallbacks = object : ContentFilterCallbacks {
			override val onLocationChanged: (loc: Location) -> Unit = {
				viewModel.changeLocation(it)
			}
			override val onTagChanged: (tag: Tag) -> Unit = {
				viewModel.changeTag(it)
			}
		}

		val listContentCoordinator = MainContentListCoordinator(
			viewModel.locationStashesContentState,
			onItemStashQuantityUpdated = { stashId, qty ->
				viewModel.updateStashQuantity(stashId, qty)
			},
			onStartStashTransfer = { item, locId ->
				viewModel.transferItemStashSheetCoordinator.showSheetForItem(item)
			}
		)

		drawContent(
			viewModel.userLocationContentState,
			viewModel.userTagsContentState,
			viewModel.quantityUnitsContentState,
			viewModel.itemsContentState,
			viewModel.itemStashesContentState,
			listContentCoordinator,
			createItemStashCoordinator,
			createLocationCoordinator,
			createTagCoordinator,
			createQuantityUnitSheetCoordinator,
			createItemCoordinator,
			contentFilterCallbacks,
			viewModel.transferItemStashSheetCoordinator
		)
	}

	@Composable
	fun drawContent(
		locationState: StateFlow<LocationContentState>,
		tagState: StateFlow<TagsContentState>,
		quantityState: StateFlow<QuantityUnitContentState>,
		itemState: StateFlow<ItemContentState>,
		itemStashState: StateFlow<ItemStashContentState>,
		listContentCoordinator: MainContentListCoordinator,
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinatorImpl,
		createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
		createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
		createItemCoordinator: CreateItemSheetCoordinatorImpl,
		contentFilterCallbacks: ContentFilterCallbacks,
		transferItemStashSheetCoordinator: TransferItemStashSheetCoordinatorImpl
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
					quantityState,
					itemState,
					itemStashState,
					listContentCoordinator,
					createItemStashSheetCoordinator,
					createLocationCoordinator,
					createTagCoordinator,
					createQuantityUnitSheetCoordinator,
					createItemCoordinator,
					transferItemStashSheetCoordinator
				)
			},
			floatingActionButton = {
				DrawAddContentFab(
					Modifier,
					createItemStashSheetCoordinator,
					createLocationCoordinator,
					createTagCoordinator,
					createItemCoordinator
				)
			},
		)
	}

	@Composable
	fun DrawBody(
		modifier: Modifier = Modifier,
		locationState: StateFlow<LocationContentState>,
		tagState: StateFlow<TagsContentState>,
		quantityState: StateFlow<QuantityUnitContentState>,
		itemState: StateFlow<ItemContentState>,
		stashState: StateFlow<ItemStashContentState>,
		listContentCoordinator: MainContentListCoordinator,
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinatorImpl,
		createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
		createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
		createItemCoordinator: CreateItemSheetCoordinatorImpl,
		transferItemStashSheetCoordinator: TransferItemStashSheetCoordinatorImpl
	) {
		val showItemStashSheet: Boolean by remember { createItemStashSheetCoordinator.showSheetState }
		val showLocationSheet: Boolean by remember { createLocationCoordinator.showSheetState }
		val showTagSheet: Boolean by remember { createTagCoordinator.showSheetState }
		val showItemSheet: Boolean by remember { createItemCoordinator.showSheetState }
		val showQuantityUnitSheet: Boolean by remember { createQuantityUnitSheetCoordinator.showSheetState }
		val showTransferSheet: Boolean by remember { transferItemStashSheetCoordinator.showSheetState }

		val tagContent = tagState.collectAsState().value
		val locationContent = locationState.collectAsState().value
		val itemStashContent = listContentCoordinator.stashContentFlow.collectAsState().value
		val forLocation = itemStashContent.data.currentLocationItemStashContent

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
						CircularProgressIndicator(
							modifier = Modifier
								.padding(8.dp, 16.dp)
								.zIndex(1f)
						)
					}
				}

				// move to main content composable since we want to add location anyways for baked-in starting point
				forLocation.isEmpty() -> {
					if (locationContent.data.currentLocation.id == STATIC_ID_LOCATION_ALL) {
						Text(
							"no items anywhere"
						)
						Button(onClick = {
							createLocationCoordinator.showSheet()
						}) {
							Text("add item")
						}
					} else {
						Text(
							"no items in ${locationContent.data.currentLocation.name}"
						)
						Button(onClick = {
							createItemStashSheetCoordinator.showSheet()
						}) {
							Text("add item")
						}
					}
				}

				else -> {
					MainStashContentList.Draw(
						modifier = Modifier.fillMaxSize(),
						currLocationId = locationContent.data.currentLocation.id,
						currTagId = tagContent.data.currentTag.id,
						listContentCoordinator
					)

					if (locationContent.isLoading) {
						// secondary spinner?
					}
				}
			}

			if (showItemStashSheet) {
				CreateItemStashModalSheet.Draw(
					Modifier,
					createItemStashSheetCoordinator,
				)
			}

			if (showLocationSheet) {
				CreateLocationModalSheet.Draw(Modifier, createLocationCoordinator, locationState)
			}

			if (showItemSheet) {
				CreateItemModalSheet.Draw(
					Modifier,
					createItemCoordinator,
					createQuantityUnitSheetCoordinator,
					createTagCoordinator,
					itemState,
					quantityState,
					tagState
				)
			}

			if (showTagSheet) {
				CreateTagModalSheet.Draw(Modifier, createTagCoordinator, tagState)
			}

			if (showQuantityUnitSheet) {
				CreateQuantityUnitModalSheet.Draw(Modifier, createQuantityUnitSheetCoordinator, quantityState)
			}

			if (showTransferSheet) {
				TransferItemStashModalSheet.Draw(Modifier, transferItemStashSheetCoordinator)
			}
		}
	}


	@Composable
	fun DrawAddContentFab(
		modifier: Modifier = Modifier,
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinatorImpl,
		createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
		createItemCoordinator: CreateItemSheetCoordinatorImpl
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
						createItemStashSheetCoordinator.showSheet()
						showMenu = false
					}
				)
				DropdownMenuItem(
					text = { Text("Add location") }, onClick = {
						createLocationCoordinator.showSheet()
						showMenu = false
					}
				)
				DropdownMenuItem(
					text = { Text("Add tag") }, onClick = {
						createTagCoordinator.showSheet()
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
		createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
	) {
		// empty
		Text(
			"no locations :("
		)
		Button(onClick = {
			createLocationCoordinator.showSheet()
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
		previewQuantityUnitsContentFlow(),
		previewItemsContentFlow(),
		previewItemStashesContentFlow(),
		MainContentListCoordinator(),
		stubCreateItemStashSheetCoordinator,
		stubCreateLocationModalSheetCoordinator,
		stubCreateTagSheetCoordinator,
		stubCreateQuantityUnitSheetCoordinator,
		stubCreateItemSheetCoordinator,
		stubContentFilterCallbacks,
		stubTransferItemStashSheetCoordinator,
	)
}