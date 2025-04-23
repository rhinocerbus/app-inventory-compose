package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.enums.SortOrder
import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.data.model.composite.FullItemData
import com.piledrive.inventory.data.model.composite.ItemWithTagsContent
import com.piledrive.inventory.repo.Item2TagsRepo
import com.piledrive.inventory.repo.ItemsRepo
import com.piledrive.inventory.repo.QuantityUnitsRepo
import com.piledrive.inventory.repo.TagsRepo
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.screens.items.content.ManageItemsContentCoordinator
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.state.TagsContentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageItemsViewModel @Inject constructor(
	private val itemsRepo: ItemsRepo,
	private val item2TagsRepo: Item2TagsRepo,
	private val tagsRepo: TagsRepo,
	private val quantityUnitsRepo: QuantityUnitsRepo,
) : ViewModel() {

	init {
		//reloadContent()
	}

	fun reloadContent() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				itemsRepo.initialize().collect {
					Timber.d("tags repo init status: $it")
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
							watchItems()
							watchItem2Tags()
							watchTags()
							watchQuantityUnits()
						}
					}
				}
			}
		}
	}

	//  region Item data
	/////////////////////////////////////////////////

	private var itemsContent: ItemContentState = ItemContentState()
	private val _itemsContentFlow = MutableStateFlow<ItemContentState>(itemsContent)
	val itemsContentFlow: StateFlow<ItemContentState> = _itemsContentFlow


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

	private fun addNewItem(slug: ItemSlug) {
		viewModelScope.launch {
			itemsRepo.addItem(slug)
		}
	}

	private fun updateItem(item: FullItemData) {
		viewModelScope.launch {
			itemsRepo.updateItemWithTags(item)
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


	//  region Tags data
	/////////////////////////////////////////////////

	private var userTagsContent: TagsContentState = TagsContentState()
	private val _userTagsContentState = MutableStateFlow<TagsContentState>(userTagsContent)
	val userTagsContentFlow: StateFlow<TagsContentState> = _userTagsContentState

	private fun watchTags() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				tagsRepo.watchTags().collect {
					Timber.d("Tags received: $it")
					userTagsContent = userTagsContent.copy(
						data = TagOptions(
							userTags = it,
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
	private val _quantityUnitsContentFlow = MutableStateFlow<QuantityUnitContentState>(quantityUnitsContent)
	val quantityUnitsContentFlow: StateFlow<QuantityUnitContentState> = _quantityUnitsContentFlow

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

	private fun addNewQuantityUnit(slug: QuantityUnitSlug) {
		viewModelScope.launch {
			quantityUnitsRepo.addQuantityUnit(slug)
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region compiled data
	/////////////////////////////////////////////////

	private var fullItemsContent: FullItemsContentState = FullItemsContentState()
	private val _fullItemsContentFlow = MutableStateFlow<FullItemsContentState>(fullItemsContent)
	val fullItemsContentFlow: StateFlow<FullItemsContentState> = _fullItemsContentFlow

	// todo - resolve this with powersync queries, relations
	private suspend fun rebuildItemsWithTags() {
		rebuildContent().debounce(500).collect { updatedContent ->
			fullItemsContent = updatedContent
			withContext(Dispatchers.Main) {
				_fullItemsContentFlow.value = fullItemsContent
			}
		}
	}

	private fun rebuildContent(): Flow<FullItemsContentState> {
		return callbackFlow {
			val tags = userTagsContent.data.userTags
			val quantityUnits = quantityUnitsContent.data.allUnits
			val items = itemsContent.data.items

			val itemsWithTags: List<FullItemData> = items.map { item ->
				val tagIdsForItem = item2Tags.filter { it.itemId == item.id }.map { it.tagId }
				val tagsForItem = tags.filter { tagIdsForItem.contains(it.id) }
				val unitForItem = quantityUnits.firstOrNull { it.id == item.unitId } ?: QuantityUnit.defaultUnitBags
				FullItemData(item, unitForItem, tagsForItem)
			}

			val sort = SortOrder.DEFAULT
			val sortDesc = true
			val sorted = when (sort) {
				SortOrder.NAME -> {
					if (sortDesc) {
						itemsWithTags.sortedByDescending { it.item.name }
					} else {
						itemsWithTags.sortedBy { it.item.name }
					}
				}

				SortOrder.LAST_ADDED -> {
					if (sortDesc) {
						itemsWithTags.sortedByDescending { it.item.createdAt }
					} else {
						itemsWithTags.sortedBy { it.item.createdAt }
					}
				}

				SortOrder.LAST_UPDATED -> {
					if (sortDesc) {
						itemsWithTags.sortedByDescending { it.item.createdAt }
					} else {
						itemsWithTags.sortedBy { it.item.createdAt }
					}
				}
			}

			val content = ItemWithTagsContent(
				sorted
			)
			val updated = fullItemsContent.copy(
				data = content
			)
			send(updated)
			close()
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val contentCoordinator = ManageItemsContentCoordinator(
		itemsSourceFlow = fullItemsContentFlow,
		createItemCoordinator = CreateItemSheetCoordinator(
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
				},
			),
			onCreateDataModel = { addNewItem(it) },
			onUpdateDataModel = { updateItem(it) },
		),
	)

	/////////////////////////////////////////////////
	//  endregion
}
