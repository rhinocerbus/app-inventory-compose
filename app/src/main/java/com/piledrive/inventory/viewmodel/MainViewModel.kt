package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.data.model.StockSlug
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.data.model.composite.ContentForLocation
import com.piledrive.inventory.data.model.composite.StockWithItem
import com.piledrive.inventory.repo.Item2TagsRepo
import com.piledrive.inventory.repo.ItemStocksRepo
import com.piledrive.inventory.repo.ItemsRepo
import com.piledrive.inventory.repo.LocationsRepo
import com.piledrive.inventory.repo.TagsRepo
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStockContentState
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.state.TagsContentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
	private val itemStocksRepo: ItemStocksRepo
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
							watchItemStocks()
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
		userLocationsContent = userLocationsContent.copy(
			data = userLocationsContent.data.copy(currentLocation = loc)
		)
		_userLocationContentState.value = userLocationsContent
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


	//  region Item stocks data
	/////////////////////////////////////////////////

	private var itemStocksContent: ItemStockContentState = ItemStockContentState()
	private val _itemStocksContentState = MutableStateFlow<ItemStockContentState>(itemStocksContent)
	val itemStocksContentState: StateFlow<ItemStockContentState> = _itemStocksContentState

	fun addNewItemStock(slug: StockSlug) {
		viewModelScope.launch {
			itemStocksRepo.addItemStock(slug)
		}
	}

	fun watchItemStocks() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				itemStocksRepo.watchItemStocks().collect {
					Timber.d("Stocks received: $it")
					itemStocksContent = itemStocksContent.copy(
						data = itemStocksContent.data.copy(itemStocks = it)
					)
					rebuildItemsWithTags()
				}
			}
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Location-specific items data
	/////////////////////////////////////////////////

	private var locationStocksContent: LocalizedContentState = LocalizedContentState()
	private val _locationStocksContentState = MutableStateFlow<LocalizedContentState>(locationStocksContent)
	val locationStocksContentState: StateFlow<LocalizedContentState> = _locationStocksContentState

	// todo - resolve this with powersync queries, relations
	private suspend fun rebuildItemsWithTags() {
		val locations = userLocationsContent.data.userLocations
		val currLocation = userLocationsContent.data.currentLocation
		val items = itemsContent.data.items
		val stocks = itemStocksContent.data.itemStocks
		val tags = userTagsContent.data.userTags

		val stocksByLocationMap = mutableMapOf<String, List<StockWithItem>>()
		locations.forEach { loc ->
			val stocksForLoc = stocks.filter { it.locationId == loc.id }.mapNotNull { stock ->
				val item = items.firstOrNull { it.id == stock.itemId } ?: return@mapNotNull null
				val tagIdsForItem = item2Tags.filter { it.itemId == item.id }.map { it.tagId }
				val tagsForItem = tags.filter { tagIdsForItem.contains(it.id) }
				StockWithItem(stock, item, tagsForItem)
			}
			stocksByLocationMap[loc.id] = stocksForLoc
		}
		val content = ContentForLocation(stocksByLocationMap)
		locationStocksContent = locationStocksContent.copy(
			data = content
		)
		withContext(Dispatchers.Main) {
			_locationStocksContentState.value = locationStocksContent
		}
	}

	/////////////////////////////////////////////////
	//  endregion
}
