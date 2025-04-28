@file:OptIn(ExperimentalCoroutinesApi::class)

package com.piledrive.inventory.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.enums.SortOrder
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.STATIC_ID_TAG_ALL
import com.piledrive.inventory.data.model.StashSlug
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.data.model.composite.FullItemData
import com.piledrive.inventory.data.model.composite.FullStashData
import com.piledrive.inventory.data.model.composite.StashesForItem
import com.piledrive.inventory.repo.Item2TagsRepo
import com.piledrive.inventory.repo.ItemStashesRepo
import com.piledrive.inventory.repo.ItemsRepo
import com.piledrive.inventory.repo.LocationsRepo
import com.piledrive.inventory.repo.QuantityUnitsRepo
import com.piledrive.inventory.repo.TagsRepo
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_item_stash.CreateItemStashSheetCoordinator
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.modal.transfer_item.TransferItemStashSheetCoordinator
import com.piledrive.inventory.ui.screens.main.bars.MainFilterAppBarCoordinator
import com.piledrive.inventory.ui.screens.main.content.MainContentListCoordinator
import com.piledrive.inventory.ui.screens.main.content.SectionedListCoordinator
import com.piledrive.inventory.ui.state.FilterOptions
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocalizedStashesPayload
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.viewmodel.nuggets.ItemsCollector
import com.piledrive.lib_compose_components.ui.coordinators.ListItemOverflowMenuCoordinator
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val locationsRepo: LocationsRepo,
	private val tagsRepo: TagsRepo,
	private val itemsRepo: ItemsRepo,
	private val item2TagsRepo: Item2TagsRepo,
	private val quantityUnitsRepo: QuantityUnitsRepo,
	private val itemStashesRepo: ItemStashesRepo,
) : ViewModel() {

	/* not init block because instantiation vs declaration order
	init {
		initDataSync()
	}
	*/

	fun initDataSync() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				locationsRepo.initialize().collect {
					Timber.d("locations repo init status: $it")
					when (it) {
						-1 -> {
							// init error
							// todo - add error ui state
						}

						0 -> {
							// started
						}

						1 -> {
							// done
							initWatches()
						}
					}
				}
			}
		}
	}

	private fun initWatches() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				val itemsSource = itemsDataCollector.itemsContentFlow
				val unitsSource = itemsDataCollector.quantityUnitsContentFlow
				val tagsSource = itemsDataCollector.userTagsContentFlow.map {
					filterAppBarCoordinator.tagsDropdownCoordinator.updateOptionsPool(it.data.tagsForFiltering)
					if (filterAppBarCoordinator.tagsDropdownCoordinator.selectedOptionState.value == null) {
						filterAppBarCoordinator.tagsDropdownCoordinator.onOptionSelected(TagOptions.defaultTag)
					}
				}
				val item2TagsSource = itemsDataCollector.item2TagsContentFlow
				val locationsSource = watchLocations()
				val stashesSource = watchItemStashes()
				merge(itemsSource, tagsSource, item2TagsSource, unitsSource, locationsSource, stashesSource)
					.debounce(500)
					.collect {
						rebuildItemsWithTags()
					}
			}
		}
	}


	//  region Location data
	/////////////////////////////////////////////////

	private var userLocationsContent: LocationContentState = LocationContentState()
	private val _userLocationsContentFlow = MutableStateFlow(userLocationsContent)

	/* supabase-only stub
	private fun loadLocations() {
		viewModelScope.launch {
			val apiLocations = repo.getAllLocations()
			val current = userLocationsContent.data
			userLocationsContent = userLocationsContent.copy(data = current.copy(userLocations = apiLocations))
			val updated = _userLocationContentState.value.copy()
			_userLocationContentState.value = updated
		}
	}
	*/

	private fun watchLocations(): Flow<Unit> {
		return locationsRepo.watchLocations().mapLatest {
			Timber.d("Locations received: $it")
			val flatLocations = listOf(LocationOptions.defaultLocation, *it.toTypedArray())
			userLocationsContent = LocationContentState(
				data = LocationOptions(
					allLocations = flatLocations,
					userLocations = it,
				),
				hasLoaded = true,
				isLoading = false
			)
			withContext(Dispatchers.Main) {
				_userLocationsContentFlow.value = userLocationsContent
				filterAppBarCoordinator.locationsDropdownCoordinator.updateOptionsPool(flatLocations)
				if (filterAppBarCoordinator.locationsDropdownCoordinator.selectedOptionState.value == null) {
					filterAppBarCoordinator.locationsDropdownCoordinator.onOptionSelected(LocationOptions.defaultLocation)
				}
			}
		}
	}

	//todo: possible add pref, or keep it session-level
	private fun changeLocation(loc: Location) {
		viewModelScope.launch {
			filterOptions = filterOptions.copy(currentLocation = loc)
			_filterOptionsFlow.value = filterOptions
			rebuildItemsWithTags()
		}
	}

	private fun addNewLocation(slug: LocationSlug) {
		viewModelScope.launch {
			locationsRepo.addLocation(slug)
		}
	}

	private fun updateLocation(location: Location) {
		viewModelScope.launch {
			locationsRepo.updateLocation(location)
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Items/Tags/Units data
	/////////////////////////////////////////////////

	private val itemsDataCollector = ItemsCollector(
		viewModelScope,
		itemsRepo.watchItems(),
		quantityUnitsRepo.watchQuantityUnits(),
		tagsRepo.watchTags(),
		item2TagsRepo.watchItem2Tags()
	)

	private fun addNewItem(item: ItemSlug) {
		viewModelScope.launch {
			itemsRepo.addItem(item)
		}
	}

	private fun updateItem(item: FullItemData) {
		viewModelScope.launch {
			itemsRepo.updateItemWithTags(item)
		}
	}

	private fun addNewQuantityUnit(slug: QuantityUnitSlug) {
		viewModelScope.launch {
			quantityUnitsRepo.addQuantityUnit(slug)
		}
	}

	private fun addNewTag(slug: TagSlug) {
		viewModelScope.launch {
			tagsRepo.addTag(slug)
		}
	}

	private var filterOptions = FilterOptions()
	private val _filterOptionsFlow = MutableStateFlow(filterOptions)
	val filterOptionsFlow: StateFlow<FilterOptions> = _filterOptionsFlow

	//todo: possible add pref, or keep it session-level
	private fun changeTag(tag: Tag) {
		viewModelScope.launch {
			filterOptions = filterOptions.copy(currentTag = tag)
			_filterOptionsFlow.value = filterOptions
			rebuildItemsWithTags()
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Item stashes data
	/////////////////////////////////////////////////

	private var itemStashesContent: ItemStashContentState = ItemStashContentState()
	private val _itemStashesContentFlow = MutableStateFlow(itemStashesContent)

	private fun addNewItemStash(slug: StashSlug) {
		viewModelScope.launch {
			itemStashesRepo.addItemStash(slug)
		}
	}

	private fun watchItemStashes(): Flow<Unit> {
		return itemStashesRepo.watchItemStashes().mapLatest {
			Timber.d("Stashes received: $it")
			itemStashesContent = itemStashesContent.copy(
				data = itemStashesContent.data.copy(itemStashes = it)
			)
			withContext(Dispatchers.Main) {
				_itemStashesContentFlow.value = itemStashesContent
			}
		}
	}

	private var quantityJobs = hashMapOf<String, Job?>()
	private fun updateStashQuantity(stashId: String, quantity: Double) {
		quantityJobs[stashId]?.cancel()
		quantityJobs[stashId] = viewModelScope.launch {
			delay(5000)
			itemStashesRepo.updateStashQuantity(stashId, quantity)
		}
	}


	/////////////////////////////////////////////////
	//  endregion


	//  region Location-specific items data
	/////////////////////////////////////////////////

	private var locationStashesContent: LocalizedContentState = LocalizedContentState()
	private val _locationStashesContentFlow = MutableStateFlow(locationStashesContent)

	// todo - resolve this with powersync queries, relations
	private suspend fun rebuildItemsWithTags() {
		val locations = userLocationsContent.data.userLocations
		val stashes = itemStashesContent.data.itemStashes
		val fullItems = itemsDataCollector.fullItemsContentFlow.value.data.fullItems

		val currLocation = filterOptions.currentLocation
		val currTag = filterOptions.currentTag

		val sort = filterAppBarCoordinator.sortDropdownCoordinator.selectedOptionState.value ?: SortOrder.DEFAULT
		val sortDesc = filterAppBarCoordinator.sortDescendingState.value

		// in: flat stashes, items, locations, etc.
		// 1) 	raw Stashes ~by~ FOR location
		// 1a)	all locations - so just all stashes
		// 1b)	specific location - filter by stash.location
		// 2)		StashesForItem by item

		val stashesForLocation = if (currLocation.id == STATIC_ID_LOCATION_ALL) {
			stashes
		} else {
			stashes.filter { it.locationId == currLocation.id }
		}

		val stashesByItem: List<StashesForItem> = stashesForLocation
			.filter { it.amount > 0.0 }
			.groupBy { it.itemId }
			.mapNotNull { input ->
				val fullItem = fullItems.firstOrNull { it.item.id == input.key } ?: return@mapNotNull null
				if (currTag.id != STATIC_ID_TAG_ALL && !fullItem.tags.map { it.id }.contains(currTag.id)) return@mapNotNull null

				val stashesForItem: List<FullStashData> = input.value
					.map { stash ->
						if (tagsForItem.firstOrNull { it.showEmpty } == null && stash.amount == 0.0) return@mapNotNull null
						val location = locations.firstOrNull { it.id == stash.locationId } ?: return@mapNotNull null
						FullStashData(stash, location)
					}
					.run {
						when (sort) {
							SortOrder.NAME -> {
								if (sortDesc) {
									this.sortedByDescending { it.location.name }
								} else {
									this.sortedBy { it.location.name }
								}
							}

							SortOrder.LAST_ADDED -> {
								if (sortDesc) {
									this.sortedByDescending { it.stash.createdAt }
								} else {
									this.sortedBy { it.stash.createdAt }
								}
							}

							SortOrder.LAST_UPDATED -> {
								if (sortDesc) {
									this.sortedByDescending { it.stash.createdAt }
								} else {
									this.sortedBy { it.stash.createdAt }
								}
							}
						}
					}
				StashesForItem(fullItem, stashesForItem)
			}

		val sorted = stashesByItem.run {
			when (sort) {
				SortOrder.NAME -> {
					if (sortDesc) {
						this.sortedByDescending { it.item.item.name }
					} else {
						this.sortedBy { it.item.item.name }
					}
				}

				SortOrder.LAST_ADDED -> {
					if (sortDesc) {
						this.sortedByDescending { it.stashes[0].stash.createdAt }
					} else {
						this.sortedBy { it.stashes[0].stash.createdAt }
					}
				}

				SortOrder.LAST_UPDATED -> {
					if (sortDesc) {
						this.sortedByDescending { it.stashes[0].stash.createdAt }
					} else {
						this.sortedBy { it.stashes[0].stash.createdAt }
					}
				}
			}
		}

		locationStashesContent = locationStashesContent.copy(
			data = LocalizedStashesPayload(sorted)
		)
		withContext(Dispatchers.Main) {
			_locationStashesContentFlow.value = locationStashesContent
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val createLocationCoordinator = CreateLocationModalSheetCoordinator(
		locationsSourceFlow = _userLocationsContentFlow,
		onCreateDataModel = {
			addNewLocation(it)
		},
		onUpdateDataModel = {
			updateLocation(it)
		}
	)

	val createItemCoordinator = CreateItemSheetCoordinator(
		itemsSourceFlow = itemsDataCollector.itemsContentFlow,
		unitsSourceFlow = itemsDataCollector.quantityUnitsContentFlow,
		tagsSourceFlow = itemsDataCollector.userTagsContentFlow,
		createTagCoordinator = CreateTagSheetCoordinator(
			itemsDataCollector.userTagsContentFlow,
			onCreateDataModel = {
				addNewTag(it)
			},
			onUpdateDataModel = {
				/*
					no-op on this screen
					should maybe launch the manage screen with a flag to auto-launch the modal and remove this coordinator entirely
				 */
			}
		),
		createQuantityUnitSheetCoordinator = CreateQuantityUnitSheetCoordinator(
			itemsDataCollector.quantityUnitsContentFlow,
			onCreateDataModel = {
				addNewQuantityUnit(it)
			},
			onUpdateDataModel = {
				// no-op on this screen
			}
		),
		onCreateDataModel = { addNewItem(it) },
		onUpdateDataModel = { updateItem(it) },
	)

	val createItemStashCoordinator = CreateItemStashSheetCoordinator(
		_itemStashesContentFlow,
		itemsDataCollector.itemsContentFlow,
		_userLocationsContentFlow,
		createItemCoordinator = createItemCoordinator,
		createLocationCoordinator = createLocationCoordinator,
		onCreateDataModel = {
			addNewItemStash(it)
		}
	)

	val transferItemStashSheetCoordinator = TransferItemStashSheetCoordinator(
		itemsSourceFlow = itemsDataCollector.itemsContentFlow,
		locationsSourceFlow = _userLocationsContentFlow,
		stashesSourceFlow = _itemStashesContentFlow,
		onCommitStashTransfer = { fromStash, toStash ->
			viewModelScope.launch {
				itemStashesRepo.performTransfer(fromStash, toStash)
			}
		}
	)

	val listContentCoordinator = MainContentListCoordinator(
		_locationStashesContentFlow,
		locationsSourceFlow = _userLocationsContentFlow,
		tagsSourceFlow = itemsDataCollector.userTagsContentFlow,
		filterOptionsFlow = filterOptionsFlow,
		itemMenuCoordinator = ListItemOverflowMenuCoordinator(),
		onItemStashQuantityUpdated = { stashId, qty ->
			updateStashQuantity(stashId, qty)
		},
		allLocationsSectionsCoordinator = SectionedListCoordinator(),
		onItemClicked = {
			createItemCoordinator.showSheetWithData(FullItemData(it.item.item, it.item.unit, it.item.tags))
		},
		onStartStashTransfer = { item, _ ->
			//todo - hook up starting location
			transferItemStashSheetCoordinator.showSheetForItem(item)
		}
	)

	val filterAppBarCoordinator = MainFilterAppBarCoordinator(
		locationsSourceFlow = _userLocationsContentFlow,
		tagsSourceFlow = itemsDataCollector.userTagsContentFlow,
		locationsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(
			externalOnOptionSelected = {
				it ?: return@ReadOnlyDropdownCoordinatorGeneric
				changeLocation(it)
			},
			excludeSelected = true,
			showSelectedState = false,
			optionTextMutator = { it.name },
			optionIdForSelectedCheck = { it.id }
		),
		tagsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(
			externalOnOptionSelected = {
				it ?: return@ReadOnlyDropdownCoordinatorGeneric
				changeTag(it)
			},
			excludeSelected = true,
			showSelectedState = false,
			optionTextMutator = { it.name },
			optionIdForSelectedCheck = { it.id }
		),
		sortDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(
			selectedOptionState = mutableStateOf(SortOrder.DEFAULT),
			dropdownOptionsState = mutableStateOf(SortOrder.entries),
			externalOnOptionSelected = {
				viewModelScope.launch {
					rebuildItemsWithTags()
				}
			},
			showSelectedState = true,
			optionIdForSelectedCheck = { it.name },
			optionTextMutator = { it.name }
		),
		sortDesc = false
	)

	/////////////////////////////////////////////////
	//  endregion
}
