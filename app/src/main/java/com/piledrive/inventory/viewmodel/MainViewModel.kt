package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.piledrive.inventory.data.model.composite.StashForItem
import com.piledrive.inventory.data.model.composite.StashForItemAtLocation
import com.piledrive.inventory.repo.Item2TagsRepo
import com.piledrive.inventory.repo.ItemStashesRepo
import com.piledrive.inventory.repo.ItemsRepo
import com.piledrive.inventory.repo.LocationsRepo
import com.piledrive.inventory.repo.QuantityUnitsRepo
import com.piledrive.inventory.repo.TagsRepo
import com.piledrive.inventory.ui.modal.transfer_item.TransferItemStashSheetCoordinator
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.state.TagsContentState
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

	fun addNewLocation(slug: LocationSlug) {
		viewModelScope.launch {
			locationsRepo.addLocation(slug)
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

	fun addNewItem(item: ItemSlug) {
		viewModelScope.launch {
			itemsRepo.addItem(item)
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

	val transferFromLocationCoordinator = ReadOnlyDropdownCoordinatorGeneric<Location>()
	val transferToLocationCoordinator = ReadOnlyDropdownCoordinatorGeneric<Location>()
	val transferItemStashSheetCoordinator = TransferItemStashSheetCoordinator(
		stashesStateSource = itemStashesContentState,
		itemStateSource = itemsContentState,
		locationsStateSource = userLocationContentState,
		fromLocationDropdownCoordinator = transferFromLocationCoordinator,
		toLocationDropdownCoordinator = transferToLocationCoordinator,
		reloadOptions = { itemId ->
			val items = itemsContent.data.items
			val quantityUnits = quantityUnitsContent.data.allUnits
			val stashes = itemStashesContent.data.itemStashes
			val locations = userLocationsContent.data.userLocations

			val rootItem = items.firstOrNull { it.id == itemId } ?: throw IllegalStateException("no item by given id")
			val compiledData = mutableListOf<StashForItemAtLocation>()
			val stashesForItem = stashes.filter { it.itemId == itemId }
			stashesForItem.forEach { stash ->
				val atLocation = locations.firstOrNull { it.id == stash.locationId } ?: run { return@forEach }
				val withUnit = quantityUnits.firstOrNull { it.id == rootItem.unitId } ?: run { return@forEach }
				compiledData.add(StashForItemAtLocation(stash = stash, location = atLocation, item = rootItem, quantityUnit = withUnit))
			}
			return@TransferItemStashSheetCoordinator compiledData
		},
		onCommitStashTransfer = { fId, fA, tId, tA ->
			viewModelScope.launch {
				itemStashesRepo.updateStashQuantity(fId, fA)
				itemStashesRepo.updateStashQuantity(tId, tA)
			}
		}
	)


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
			stashesByLocationMap.values.forEach { items ->
				items.forEach { item ->
					val oldStash = consolidatedMap[item.item.id]
					if (oldStash != null) {
						consolidatedMap[item.item.id] =
							oldStash.copy(stash = oldStash.stash.copy(amount = oldStash.stash.amount + item.stash.amount))
					} else {
						consolidatedMap[item.item.id] = item
					}
				}
			}
			consolidatedMap.values.toList()
		} else {
			stashesByLocationMap[currLocation.id] ?: listOf()
		}

		val filteredByTag = if (currTag.id == STATIC_ID_TAG_ALL) {
			stashesForLocation
		} else {
			stashesForLocation.filter { it.tags.contains(currTag) }
		}

		val content = ContentForLocation(
			currLocation.id,
			currentLocationItemStashContent = filteredByTag
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
}
