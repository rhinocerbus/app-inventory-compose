package com.piledrive.inventory.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.enums.SortOrder
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
	private val _userLocationsContentFlow = MutableStateFlow<LocationContentState>(userLocationsContent)
	val userLocationsContentFlow: StateFlow<LocationContentState> = _userLocationsContentFlow

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
						_userLocationsContentFlow.value = userLocationsContent
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
			_userLocationsContentFlow.value = userLocationsContent
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
	private val _userTagsContentFlow = MutableStateFlow<TagsContentState>(userTagsContent)
	val userTagsContentFlow: StateFlow<TagsContentState> = _userTagsContentFlow

	private fun watchTags() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				tagsRepo.watchTags().collect {
					Timber.d("Tags received: $it")
					userTagsContent = userTagsContent.copy(
						data = TagOptions(
							userTags = it,
							currentTag = userTagsContent.data.currentTag
						),
						hasLoaded = true,
						isLoading = false
					)
					withContext(Dispatchers.Main) {
						_userTagsContentFlow.value = userTagsContent
						filterAppBarCoordinator.tagsDropdownCoordinator.updateOptionsPool(userTagsContent.data.allTags)
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
			_userTagsContentFlow.value = userTagsContent
			rebuildItemsWithTags()
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Quantity units data
	/////////////////////////////////////////////////

	private var quantityUnitsContent: QuantityUnitContentState = QuantityUnitContentState()
	private val _quantityUnitsContentFlow = MutableStateFlow<QuantityUnitContentState>(quantityUnitsContent)
	val quantityUnitsContentFlow: StateFlow<QuantityUnitContentState> = _quantityUnitsContentFlow

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
						data = quantityUnitsContent.data.copy(customUnits = it)
					)
					withContext(Dispatchers.Main) {
						_quantityUnitsContentFlow.value = quantityUnitsContent
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
	private val _itemsContentFlow = MutableStateFlow<ItemContentState>(itemsContent)
	val itemsContentFlow: StateFlow<ItemContentState> = _itemsContentFlow

	private fun addNewItem(item: ItemSlug) {
		viewModelScope.launch {
			itemsRepo.addItem(item)
		}
	}

	private fun updateItem(item: ItemWithTags) {
		viewModelScope.launch {
			itemsRepo.updateItemWithTags(item)
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
						_itemsContentFlow.value = itemsContent
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
	private val _itemStashesContentFlow = MutableStateFlow<ItemStashContentState>(itemStashesContent)
	val itemStashesContentFlow: StateFlow<ItemStashContentState> = _itemStashesContentFlow

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
						_itemStashesContentFlow.value = itemStashesContent
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
	private val _locationStashesContentFlow = MutableStateFlow<LocalizedContentState>(locationStashesContent)
	val locationStashesContentFlow: StateFlow<LocalizedContentState> = _locationStashesContentFlow

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
			_locationStashesContentFlow.value = locationStashesContent
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val createLocationCoordinator = CreateLocationModalSheetCoordinator(
		locationsSourceFlow = userLocationsContentFlow,
		onCreateDataModel = {
			addNewLocation(it)
		},
		onUpdateDataModel = {
			updateLocation(it)
		}
	)

	val createItemCoordinator = CreateItemSheetCoordinator(
		itemsSourceFlow = itemsContentFlow,
		unitsSourceFlow = quantityUnitsContentFlow,
		tagsSourceFlow = userTagsContentFlow,
		createTagCoordinator = CreateTagSheetCoordinator(
			userTagsContentFlow,
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
			quantityUnitsContentFlow,
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
		itemStashesContentFlow,
		itemsContentFlow,
		userLocationsContentFlow,
		createItemCoordinator = createItemCoordinator,
		createLocationCoordinator = createLocationCoordinator,
		onCreateDataModel = {
			addNewItemStash(it)
		}
	)

	val transferItemStashSheetCoordinator = TransferItemStashSheetCoordinator(
		itemsSourceFlow = itemsContentFlow,
		unitsSourceFlow = quantityUnitsContentFlow,
		locationsSourceFlow = userLocationsContentFlow,
		stashesSourceFlow = itemStashesContentFlow,
		onCommitStashTransfer = { fromStash, toStash ->
			viewModelScope.launch {
				itemStashesRepo.performTransfer(fromStash, toStash)
			}
		}
	)

	val listContentCoordinator = MainContentListCoordinator(
		locationStashesContentFlow,
		locationsSourceFlow = userLocationsContentFlow,
		tagsSourceFlow = userTagsContentFlow,
		itemMenuCoordinator = ListItemOverflowMenuCoordinator(),
		onItemStashQuantityUpdated = { stashId, qty ->
			updateStashQuantity(stashId, qty)
		},
		onItemClicked = {
			createItemCoordinator.showSheetWithData(ItemWithTags(it.item, it.tags, it.quantityUnit))
		},
		onStartStashTransfer = { item, locId ->
			transferItemStashSheetCoordinator.showSheetForItem(item)
		}
	)

	val filterAppBarCoordinator = MainFilterAppBarCoordinator(
		locationsSourceFlow = _userLocationsContentFlow,
		tagsSourceFlow = userTagsContentFlow,
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
