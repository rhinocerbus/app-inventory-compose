@file:OptIn(ExperimentalCoroutinesApi::class)

package com.piledrive.inventory.viewmodel.nuggets

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.composite.FullItemData
import com.piledrive.inventory.data.model.composite.FullItemsContent
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.state.TagsContentState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ItemsCollector(
	coroutineScope: CoroutineScope,
	itemsSourceFlow: Flow<List<Item>>,
	unitsSourceFlow: Flow<List<QuantityUnit>>,
	tagsSourceFlow: Flow<List<Tag>>,
	items2TagsSourceFlow: Flow<List<Item2Tag>>,
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val itemsSource = watchItems(itemsSourceFlow)
			val unitsSource = watchQuantityUnits(unitsSourceFlow)
			val tagsSource = watchTags(tagsSourceFlow)
			val item2TagsSource = watchItem2Tags(items2TagsSourceFlow)

			merge(itemsSource, tagsSource, item2TagsSource, unitsSource)
				.debounce(500)
				.collect {
					rebuildItemsWithTags()
				}
		}
	}


	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var itemsContent: ItemContentState = ItemContentState()
	private val _itemsContentFlow = MutableStateFlow(itemsContent)
	val itemsContentFlow: StateFlow<ItemContentState> = _itemsContentFlow

	private fun watchItems(itemsSourceFlow: Flow<List<Item>>): Flow<Unit> {
		return itemsSourceFlow.mapLatest {
			Timber.d("Items received: $it")
			itemsContent = itemsContent.copy(
				data = itemsContent.data.copy(items = it)
			)
			withContext(Dispatchers.Main) {
				_itemsContentFlow.value = itemsContent
			}
		}
	}

	private var quantityUnitsContent: QuantityUnitContentState = QuantityUnitContentState()
	private val _quantityUnitsContentFlow = MutableStateFlow<QuantityUnitContentState>(quantityUnitsContent)
	val quantityUnitsContentFlow: StateFlow<QuantityUnitContentState> = _quantityUnitsContentFlow

	private fun watchQuantityUnits(unitsSourceFlow: Flow<List<QuantityUnit>>): Flow<Unit> {
		return unitsSourceFlow.mapLatest {
			Timber.d("Units received: $it")
			quantityUnitsContent = quantityUnitsContent.copy(
				data = quantityUnitsContent.data.copy(customUnits = it)
			)
			withContext(Dispatchers.Main) {
				_quantityUnitsContentFlow.value = quantityUnitsContent
			}
		}
	}

	private var userTagsContent: TagsContentState = TagsContentState()
	private val _userTagsContentFlow = MutableStateFlow(userTagsContent)
	val userTagsContentFlow: StateFlow<TagsContentState> = _userTagsContentFlow

	private fun watchTags(tagsSourceFlow: Flow<List<Tag>>): Flow<Unit> {
		return tagsSourceFlow.mapLatest {
			Timber.d("Tags received: $it")
			userTagsContent = userTagsContent.copy(
				data = TagOptions(
					userTags = it,
				),
				hasLoaded = true,
				isLoading = false
			)
			withContext(Dispatchers.Main) {
				_userTagsContentFlow.value = userTagsContent
			}
		}
	}

	private val item2Tags = mutableListOf<Item2Tag>()
	private val _item2TagsContentFlow = MutableStateFlow(item2Tags)
	val item2TagsContentFlow: StateFlow<List<Item2Tag>> = _item2TagsContentFlow

	private fun watchItem2Tags(items2TagsSourceFlow: Flow<List<Item2Tag>>): Flow<Unit> {
		return items2TagsSourceFlow.mapLatest {
			Timber.d("Items2Tags received: $it")
			item2Tags.clear()
			item2Tags.addAll(it)
			withContext(Dispatchers.Main) {
				_item2TagsContentFlow.value = item2Tags
			}
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Composite data outputs
	/////////////////////////////////////////////////

	private var fullItemsContent: FullItemsContentState = FullItemsContentState()
	private val _fullItemsContentFlow = MutableStateFlow<FullItemsContentState>(fullItemsContent)
	val fullItemsContentFlow: StateFlow<FullItemsContentState> = _fullItemsContentFlow

	// todo - resolve this with powersync queries, relations
	// todo - add optional state for current tag to filter by to keep optimization in mainviewmodel
	private suspend fun rebuildItemsWithTags() {
		val items = itemsContent.data.items
		val quantityUnits = quantityUnitsContent.data.allUnits
		val tags = userTagsContent.data.userTags
		val item2Tags = item2Tags

		val unsortedFullItems: List<FullItemData> = items.map { item ->
			val tagIdsForItem = item2Tags.filter { it.itemId == item.id }.map { it.tagId }
			val tagsForItem = tags.filter { tagIdsForItem.contains(it.id) }
			val unitForItem = quantityUnits.firstOrNull { it.id == item.unitId } ?: QuantityUnit.defaultUnitBags
			FullItemData(item, unitForItem, tagsForItem)
		}
		val content = FullItemsContent(
			unsortedFullItems
		)
		val updated = fullItemsContent.copy(
			data = content
		)
		withContext(Dispatchers.Main) {
			_fullItemsContentFlow.value = updated
		}
	}

	/////////////////////////////////////////////////
	//  endregion
}