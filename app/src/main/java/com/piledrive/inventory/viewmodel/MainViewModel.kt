package com.piledrive.inventory.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.enums.SortOrder
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Item2Tag
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
import com.piledrive.inventory.data.model.composite.ContentForLocation
import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.data.model.composite.StashForItem
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
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.lib_compose_components.ui.coordinators.ListItemOverflowMenuCoordinator
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

	init {
		//reloadContent()
	}

	fun reloadContent() {
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
							watchLocations()
							watchTags()
							watchItems()
							watchItem2Tags()
							watchItemStashes()
							watchQuantityUnits()
						}
					}
				}
			}
		}
	}


	//  region Location data
	/////////////////////////////////////////////////

	private var userLocationsContent: LocationContentState = LocationContentState()
	private val _userLocationContentState = MutableStateFlow<LocationContentState>(userLocationsContent)
	val userLocationContentState: StateFlow<LocationContentState> = _userLocationContentState

	/*
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

	private fun watchLocations() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				locationsRepo.watchLocations().collect {
					Timber.d("Locations received: $it")
					val flatLocations = listOf(LocationOptions.defaultLocation, *it.toTypedArray())
					userLocationsContent = LocationContentState(
						data = LocationOptions(
							allLocations = flatLocations,
							userLocations = it,
							currentLocation = userLocationsContent.data.currentLocation
						),
						hasLoaded = true,
						isLoading = false
					)
					withContext(Dispatchers.Main) {
						_userLocationContentState.value = userLocationsContent
						filterAppBarCoordinator.locationsDropdownCoordinator.updateOptionsPool(flatLocations)
						if (filterAppBarCoordinator.locationsDropdownCoordinator.selectedOptionState.value == null) {
							filterAppBarCoordinator.locationsDropdownCoordinator.onOptionSelected(LocationOptions.defaultLocation)
						}
					}
				}
			}
		}
	}

	//todo: possible add pref, or keep it session-level
	fun changeLocation(loc: Location) {
		viewModelScope.launch {
			userLocationsContent = userLocationsContent.copy(
				data = userLocationsContent.data.copy(currentLocation = loc)
			)
			_userLocationContentState.value = userLocationsContent
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


	//  region Tags data
	/////////////////////////////////////////////////

	private var userTagsContent: TagsContentState = TagsContentState()
	private val _userTagsContentState = MutableStateFlow<TagsContentState>(userTagsContent)
	val userTagsContentState: StateFlow<TagsContentState> = _userTagsContentState

	private fun watchTags() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				tagsRepo.watchTags().collect {
					Timber.d("Tags received: $it")
					val flatTags = listOf(TagOptions.defaultTag, *it.toTypedArray())
					userTagsContent = TagsContentState(
						data = TagOptions(
							allTags = flatTags,
							userTags = it,
							currentTag = userTagsContent.data.currentTag
						),
						hasLoaded = true,
						isLoading = false
					)
					withContext(Dispatchers.Main) {
						_userTagsContentState.value = userTagsContent
						filterAppBarCoordinator.tagsDropdownCoordinator.updateOptionsPool(flatTags)
						if (filterAppBarCoordinator.tagsDropdownCoordinator.selectedOptionState.value == null) {
							filterAppBarCoordinator.tagsDropdownCoordinator.onOptionSelected(TagOptions.defaultTag)
						}
					}
					rebuildItemsWithTags()
				}
			}
		}
	}

	fun addNewTag(slug: TagSlug) {
		viewModelScope.launch {
			tagsRepo.addTag(slug)
		}
	}

	//todo: possible add pref, or keep it session-level
	fun changeTag(tag: Tag) {
		viewModelScope.launch {
			userTagsContent = userTagsContent.copy(
				data = userTagsContent.data.copy(currentTag = tag)
			)
			_userTagsContentState.value = userTagsContent
			rebuildItemsWithTags()
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Quantity units data
	/////////////////////////////////////////////////

	private var quantityUnitsContent: QuantityUnitContentState = QuantityUnitContentState()
	private val _quantityUnitsContentState = MutableStateFlow<QuantityUnitContentState>(quantityUnitsContent)
	val quantityUnitsContentState: StateFlow<QuantityUnitContentState> = _quantityUnitsContentState

	fun addNewQuantityUnit(slug: QuantityUnitSlug) {
		viewModelScope.launch {
			quantityUnitsRepo.addQuantityUnit(slug)
		}
	}

	private fun watchQuantityUnits() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				quantityUnitsRepo.watchQuantityUnits().collect {
					Timber.d("Units received: $it")
					quantityUnitsContent = quantityUnitsContent.copy(
						data = quantityUnitsContent.data.copy(allUnits = QuantityUnit.defaultSet + it)
					)
					withContext(Dispatchers.Main) {
						_quantityUnitsContentState.value = quantityUnitsContent
					}
					rebuildItemsWithTags()
				}
			}
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Items data
	/////////////////////////////////////////////////

	private var itemsContent: ItemContentState = ItemContentState()
	private val _itemsContentState = MutableStateFlow<ItemContentState>(itemsContent)
	val itemsContentState: StateFlow<ItemContentState> = _itemsContentState

	private fun addNewItem(item: ItemSlug) {
		viewModelScope.launch {
			itemsRepo.addItem(item)
		}
	}

	private fun updateItem(item: Item, tagIds: List<String>) {
		viewModelScope.launch {
			itemsRepo.updateItemWithTags(item, tagIds)
		}
	}

	private fun watchItems() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				itemsRepo.watchItems().collect {
					Timber.d("Items received: $it")
					itemsContent = itemsContent.copy(
						data = itemsContent.data.copy(items = it)
					)
					withContext(Dispatchers.Main) {
						_itemsContentState.value = itemsContent
					}
					rebuildItemsWithTags()
				}
			}
		}
	}

	private val item2Tags = mutableListOf<Item2Tag>()
	private fun watchItem2Tags() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				item2TagsRepo.watchItem2Tags().collect {
					Timber.d("Items2Tags received: $it")
					item2Tags.clear()
					item2Tags.addAll(it)
					rebuildItemsWithTags()
				}
			}
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Item stashes data
	/////////////////////////////////////////////////

	private var itemStashesContent: ItemStashContentState = ItemStashContentState()
	private val _itemStashesContentState = MutableStateFlow<ItemStashContentState>(itemStashesContent)
	val itemStashesContentState: StateFlow<ItemStashContentState> = _itemStashesContentState

	fun addNewItemStash(slug: StashSlug) {
		viewModelScope.launch {
			itemStashesRepo.addItemStash(slug)
		}
	}

	fun watchItemStashes() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				itemStashesRepo.watchItemStashes().collect {
					Timber.d("Stashes received: $it")
					itemStashesContent = itemStashesContent.copy(
						data = itemStashesContent.data.copy(itemStashes = it)
					)
					withContext(Dispatchers.Main) {
						_itemStashesContentState.value = itemStashesContent
					}
					rebuildItemsWithTags()
				}
			}
		}
	}

	private var quantityJobs = hashMapOf<String, Job?>()
	fun updateStashQuantity(stashId: String, quantity: Double) {
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
	private val _locationStashesContentState = MutableStateFlow<LocalizedContentState>(locationStashesContent)
	val locationStashesContentState: StateFlow<LocalizedContentState> = _locationStashesContentState

	// todo - resolve this with powersync queries, relations
	private suspend fun rebuildItemsWithTags() {
		val locations = userLocationsContent.data.userLocations
		val currLocation = userLocationsContent.data.currentLocation
		val tags = userTagsContent.data.userTags
		val currTag = userTagsContent.data.currentTag
		val quantityUnits = quantityUnitsContent.data.allUnits
		val items = itemsContent.data.items
		val stashes = itemStashesContent.data.itemStashes

		val stashesByLocationMap = mutableMapOf<String, List<StashForItem>>()
		locations.forEach { loc ->
			val stashesForLoc = stashes.filter { it.locationId == loc.id }.mapNotNull { stash ->
				val item = items.firstOrNull { it.id == stash.itemId } ?: return@mapNotNull null
				val tagIdsForItem = item2Tags.filter { it.itemId == item.id }.map { it.tagId }
				val tagsForItem = tags.filter { tagIdsForItem.contains(it.id) }
				val unitForItem = quantityUnits.firstOrNull { it.id == item.unitId } ?: QuantityUnit.defaultUnitBags
				StashForItem(stash, item, tagsForItem, unitForItem)
			}
			stashesByLocationMap[loc.id] = stashesForLoc
		}

		val stashesForLocation = if (currLocation.id == STATIC_ID_LOCATION_ALL) {
			val consolidatedMap = mutableMapOf<String, StashForItem>()
			stashesByLocationMap.values.forEach { s4is ->
				s4is.forEach { s4i ->
					val oldStash = consolidatedMap[s4i.item.id]
					if (oldStash != null) {
						consolidatedMap[s4i.item.id] =
							oldStash.copy(stash = oldStash.stash.copy(amount = oldStash.stash.amount + s4i.stash.amount))
					} else {
						consolidatedMap[s4i.item.id] = s4i
					}
				}
			}
			consolidatedMap.values.toList()
		} else {
			stashesByLocationMap[currLocation.id] ?: listOf()
		}

		val unsortedByTag = if (currTag.id == STATIC_ID_TAG_ALL) {
			stashesForLocation
		} else {
			stashesForLocation.filter { it.tags.contains(currTag) }
		}

		val sort = filterAppBarCoordinator.sortDropdownCoordinator.selectedOptionState.value ?: SortOrder.DEFAULT
		val sortDesc = filterAppBarCoordinator.sortDescendingState.value
		val sorted = when (sort) {
			SortOrder.NAME -> {
				if (sortDesc) {
					unsortedByTag.sortedByDescending { it.item.name }
				} else {
					unsortedByTag.sortedBy { it.item.name }
				}
			}

			SortOrder.LAST_ADDED -> {
				if (sortDesc) {
					unsortedByTag.sortedByDescending { it.stash.createdAt }
				} else {
					unsortedByTag.sortedBy { it.item.createdAt }
				}
			}

			SortOrder.LAST_UPDATED -> {
				if (sortDesc) {
					unsortedByTag.sortedByDescending { it.item.name }
				} else {
					unsortedByTag.sortedBy { it.item.name }
				}
			}
		}

		val content = ContentForLocation(
			currLocation.id,
			currentLocationItemStashContent = sorted
		)
		locationStashesContent = locationStashesContent.copy(
			data = content
		)
		withContext(Dispatchers.Main) {
			_locationStashesContentState.value = locationStashesContent
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val createLocationCoordinator = CreateLocationModalSheetCoordinator(
		locationState = userLocationContentState,
		onAddLocation = {
			addNewLocation(it)
		},
		onUpdateLocation = {
			updateLocation(it)
		}
	)

	val createTagCoordinator = CreateTagSheetCoordinator(
		userTagsContentState,
		onAddTag = {
			addNewTag(it)
		},
		onUpdateTag = {
			/*
				no-op on this screen 
				should maybe launch the manage screen with a flag to auto-launch the modal and remove this coordinator entirely
			 */
		}
	)

	val createItemCoordinator = CreateItemSheetCoordinator(
		itemState = itemsContentState,
		quantityContentState = quantityUnitsContentState,
		tagsContentState = userTagsContentState,
		onAddItem = { addNewItem(it) },
		onUpdateItem = { item, tagIds -> updateItem(item, tagIds)},
		onLaunchAddTag = { createTagCoordinator.showSheet() },
		onLaunchAddUnit = { createQuantityUnitSheetCoordinator.showSheet() }
	)

	val createQuantityUnitSheetCoordinator = CreateQuantityUnitSheetCoordinator(
		quantityUnitsContentState,
		onAddQuantityUnit = {
			addNewQuantityUnit(it)
		},
		onUpdateQuantityUnit = {
			// no-op on this screen
		}
	)

	val createItemStashCoordinator = CreateItemStashSheetCoordinator(
		itemStashesContentState,
		itemsContentState,
		userLocationContentState,
		onAddItemToLocation = {
			addNewItemStash(it)
		},
		onLaunchCreateItem = {
			createItemCoordinator.showSheet()
		},
		onLaunchCreateLocation = {
			createLocationCoordinator.showSheet()
		}
	)

	val transferItemStashSheetCoordinator = TransferItemStashSheetCoordinator(
		itemsSource = itemsContentState,
		unitsSource = quantityUnitsContentState,
		locationsSource = userLocationContentState,
		stashesSource = itemStashesContentState,
		onCommitStashTransfer = { fromStash, toStash ->
			viewModelScope.launch {
				itemStashesRepo.performTransfer(fromStash, toStash)
			}
		}
	)

	val listContentCoordinator = MainContentListCoordinator(
		locationStashesContentState,
		locationState = userLocationContentState,
		tagState = userTagsContentState,
		itemMenuCoordinator = ListItemOverflowMenuCoordinator(),
		onItemStashQuantityUpdated = { stashId, qty ->
			updateStashQuantity(stashId, qty)
		},
		onItemClicked = {
			createItemCoordinator.showSheetForItem(ItemWithTags(it.item, it.tags, it.quantityUnit))
		} ,
		onStartStashTransfer = { item, locId ->
			transferItemStashSheetCoordinator.showSheetForItem(item)
		}
	)

	val filterAppBarCoordinator = MainFilterAppBarCoordinator(
		locationState = _userLocationContentState,
		tagState = userTagsContentState,
		locationsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(
			externalOnOptionSelected = {
				it ?: return@ReadOnlyDropdownCoordinatorGeneric
				changeLocation(it)
			},
			excludeSelected = true,
			showSelectedState = false,
			optionTextMutator = { "${it.name}" },
			optionIdForSelectedCheck = { it.id }
		),
		tagsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(
			externalOnOptionSelected = {
				it ?: return@ReadOnlyDropdownCoordinatorGeneric
				changeTag(it)
			},
			excludeSelected = true,
			showSelectedState = false,
			optionTextMutator = { "${it.name}" },
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
			optionTextMutator = { "${it.name}" }
		),
		sortDesc = false
	)

	/////////////////////////////////////////////////
	//  endregion
}
