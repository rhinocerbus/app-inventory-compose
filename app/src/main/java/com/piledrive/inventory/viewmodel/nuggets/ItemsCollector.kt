@file:OptIn(ExperimentalCoroutinesApi::class)

package com.piledrive.inventory.viewmodel.nuggets

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.state.TagsContentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class ItemsCollector(
	private val itemsSourceFlow: Flow<List<Item>>,
	private val unitsSourceFlow: Flow<List<QuantityUnit>>,
	private val tagsSourceFlow: Flow<List<Tag>>,
	private val items2TagsSourceFlow: Flow<List<Item2Tag>>,
) {

	init {
		watchItems()
		watchQuantityUnits()
		watchTags()
		watchItem2Tags()
	}

	private var itemsContent: ItemContentState = ItemContentState()
	private val _itemsContentFlow = MutableStateFlow(itemsContent)
	val itemsContentFlow: StateFlow<ItemContentState> = _itemsContentFlow

	private fun watchItems(): Flow<Unit> {
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

	private fun watchQuantityUnits(): Flow<Unit> {
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

	private fun watchTags(): Flow<Unit> {
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

	private fun watchItem2Tags(): Flow<Unit> {
		return items2TagsSourceFlow.mapLatest {
			Timber.d("Items2Tags received: $it")
			item2Tags.clear()
			item2Tags.addAll(it)
			withContext(Dispatchers.Main) {
				_item2TagsContentFlow.value = item2Tags
			}
		}
	}
}