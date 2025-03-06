package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.StockSlug
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.repo.Item2TagsRepo
import com.piledrive.inventory.repo.ItemStocksRepo
import com.piledrive.inventory.repo.ItemsRepo
import com.piledrive.inventory.repo.LocationsRepo
import com.piledrive.inventory.repo.TagsRepo
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStockContentState
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

	fun addNewLocation(name: String) {
		viewModelScope.launch {
			locationsRepo.addLocation(name)
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region tags
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

	fun addNewTag(name: String) {
		viewModelScope.launch {
			tagsRepo.addTag(name)
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Items
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


	//  region items
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

	// todo - resolve this with powersync queries, relations
	private suspend fun rebuildItemsWithTags() {
		val tags = userTagsContent.data.userTags
		val items = itemsContent.data.items
		val tagsByItemsMap = mutableMapOf<String, List<Tag>>()
		items.forEach { item ->
			val tagIdsForItem = item2Tags.filter { it.itemId == item.id }.map { it.tagId }
			val tagsForItem = tags.filter { tagIdsForItem.contains(it.id) }
			tagsByItemsMap[item.id] = tagsForItem
			Timber.d("added ${tagsForItem.size} tags for item ${item.name}")
		}
		itemsContent = itemsContent.copy(
			data = itemsContent.data.copy(tagsByItemsMap = tagsByItemsMap)
		)
		withContext(Dispatchers.Main) {
			_itemsContentState.value = itemsContent
		}
	}

	/////////////////////////////////////////////////
	//  endregion
}
