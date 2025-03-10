@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.SuggestionChip
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
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.STATIC_ID_TAG_ALL
import com.piledrive.inventory.data.model.StashSlug
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.ui.callbacks.ContentFilterCallbacks
import com.piledrive.inventory.ui.callbacks.stubContentFilterCallbacks
import com.piledrive.inventory.ui.modal.AddItemStashCallbacks
import com.piledrive.inventory.ui.modal.CreateItemCallbacks
import com.piledrive.inventory.ui.modal.CreateItemModalSheet
import com.piledrive.inventory.ui.modal.CreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.CreateItemStashModalSheet
import com.piledrive.inventory.ui.modal.CreateItemStashSheetCoordinator
import com.piledrive.inventory.ui.modal.CreateLocationCallbacks
import com.piledrive.inventory.ui.modal.CreateLocationModalSheet
import com.piledrive.inventory.ui.modal.CreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.modal.CreateTagCallbacks
import com.piledrive.inventory.ui.modal.CreateTagModalSheet
import com.piledrive.inventory.ui.modal.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocalizedContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
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
			createLocationCallbacks = object : CreateLocationCallbacks {
				override val onAddLocation: (slug: LocationSlug) -> Unit = {
					viewModel.addNewLocation(it)
				}
			}
		)

		val createTagCoordinator = CreateTagSheetCoordinator(
			createTagCallbacks = object : CreateTagCallbacks {
				override val onAddTag: (slug: TagSlug) -> Unit = {
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

		val createItemStashCoordinator = CreateItemStashSheetCoordinator(
			createItemStashCallbacks = object : AddItemStashCallbacks {
				override val onAddItemToLocation: (slug: StashSlug) -> Unit = {
					viewModel.addNewItemStash(it)
				}
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

		val stashesListCallbacks = object : MainStashContentListCallbacks {
			override val onItemStashQuantityUpdated: () -> Unit = {
				viewModel
			}
		}

		drawContent(
			viewModel.userLocationContentState,
			viewModel.userTagsContentState,
			viewModel.itemsContentState,
			viewModel.itemStashesContentState,
			viewModel.locationStashesContentState,
			stashesListCallbacks,
			createItemStashCoordinator,
			createLocationCoordinator,
			createTagCoordinator,
			createItemCoordinator,
			contentFilterCallbacks
		)
	}

	@Composable
	fun drawContent(
		locationState: StateFlow<LocationContentState>,
		tagState: StateFlow<TagsContentState>,
		itemState: StateFlow<ItemContentState>,
		itemStashState: StateFlow<ItemStashContentState>,
		localizedStashesContent: StateFlow<LocalizedContentState>,
		stashesListCallbacks: MainStashContentListCallbacks,
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinator,
		createLocationCoordinator: CreateLocationModalSheetCoordinator,
		createTagCoordinator: CreateTagSheetCoordinator,
		createItemCoordinator: CreateItemSheetCoordinator,
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
					localizedStashesContent,
					stashesListCallbacks,
					createItemStashSheetCoordinator,
					createLocationCoordinator,
					createTagCoordinator,
					createItemCoordinator,
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
		itemState: StateFlow<ItemContentState>,
		localizedStashesContent: StateFlow<LocalizedContentState>,
		stashesListCallbacks: MainStashContentListCallbacks,
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinator,
		createLocationCoordinator: CreateLocationModalSheetCoordinator,
		createTagCoordinator: CreateTagSheetCoordinator,
		createItemCoordinator: CreateItemSheetCoordinator,
	) {
		val showItemStashSheet: Boolean by remember { createItemStashSheetCoordinator.showSheetState }
		val showLocationSheet: Boolean by remember { createLocationCoordinator.showSheetState }
		val showTagSheet: Boolean by remember { createTagCoordinator.showSheetState }
		val showItemSheet: Boolean by remember { createItemCoordinator.showSheetState }

		val tagContent = tagState.collectAsState().value
		val locationContent = locationState.collectAsState().value
		val itemStashContent = localizedStashesContent.collectAsState().value
		val forLocation = if (locationContent.data.currentLocation.id == STATIC_ID_LOCATION_ALL) {
			itemStashContent.data.flatContent
		} else {
			itemStashContent.data.locationsScopedContent[locationContent.data.currentLocation.id] ?: listOf()
		}

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

				forLocation.isEmpty() -> {
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
							createItemStashSheetCoordinator.showSheetState.value = true
						}) {
							Text("add item")
						}
					}
				}

				else -> {
					// todo - fully hoist state-based content up to viewmodel
					val finalItems = if (tagContent.data.currentTag.id == STATIC_ID_TAG_ALL) {
						forLocation
					} else {
						forLocation.filter { it.tags.map { it.id }.contains(tagContent.data.currentTag.id) }
					}
					MainStashContentList.Draw(
						modifier = Modifier.fillMaxSize(),
						stashes = finalItems,
						stashesListCallbacks
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
					createItemCoordinator,
					createLocationCoordinator,
					itemState,
					locationState
				)
			}

			if (showLocationSheet) {
				CreateLocationModalSheet.Draw(Modifier, createLocationCoordinator)
			}

			if (showItemSheet) {
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
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinator,
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
						createItemStashSheetCoordinator.showSheetState.value = true
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
		previewItemStashesContentFlow(),
		previewLocalizedContentFlow(),
		stubMainStashContentListCallbacks,
		CreateItemStashSheetCoordinator(),
		CreateLocationModalSheetCoordinator(),
		CreateTagSheetCoordinator(),
		CreateItemSheetCoordinator(),
		stubContentFilterCallbacks
	)
}