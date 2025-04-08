package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.enums.SortOrder
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.data.model.composite.FullItemsContent
import com.piledrive.inventory.data.model.composite.ItemWithTags
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
	private val _itemsContentState = MutableStateFlow<ItemContentState>(itemsContent)
	val itemsContentState: StateFlow<ItemContentState> = _itemsContentState


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

	private fun addNewItem(slug: ItemSlug) {
		viewModelScope.launch {
			itemsRepo.addItem(slug)
		}
	}

	private fun updateItem(item: Item, tagIds: List<String>) {
		viewModelScope.launch {
			itemsRepo.updateItemWithTags(item, tagIds)
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
	val userTagsContentState: StateFlow<TagsContentState> = _userTagsContentState

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
	private val _quantityUnitsContentState = MutableStateFlow<QuantityUnitContentState>(quantityUnitsContent)
	val quantityUnitsContentState: StateFlow<QuantityUnitContentState> = _quantityUnitsContentState

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
	private val _fullItemsContentState = MutableStateFlow<FullItemsContentState>(fullItemsContent)
	val fullItemsContentState: StateFlow<FullItemsContentState> = _fullItemsContentState

	// todo - resolve this with powersync queries, relations
	private suspend fun rebuildItemsWithTags() {
		val tags = userTagsContent.data.userTags
		val quantityUnits = quantityUnitsContent.data.allUnits
		val items = itemsContent.data.items

		val itemsWithTags: List<ItemWithTags> = items.map { item ->
			val tagIdsForItem = item2Tags.filter { it.itemId == item.id }.map { it.tagId }
			val tagsForItem = tags.filter { tagIdsForItem.contains(it.id) }
			val unitForItem = quantityUnits.firstOrNull { it.id == item.unitId } ?: QuantityUnit.defaultUnitBags
			ItemWithTags(item, tagsForItem, unitForItem)
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

		val content = FullItemsContent(
			sorted
		)
		fullItemsContent = fullItemsContent.copy(
			data = content
		)
		withContext(Dispatchers.Main) {
			_fullItemsContentState.value = fullItemsContent
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val contentCoordinator = ManageItemsContentCoordinator(
		itemState = fullItemsContentState,
		createItemCoordinator = CreateItemSheetCoordinator(
			itemState = itemsContentState,
			quantityContentState = quantityUnitsContentState,
			tagsContentState = userTagsContentState,
			createTagCoordinator = CreateTagSheetCoordinator(
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
			),
			createQuantityUnitSheetCoordinator = CreateQuantityUnitSheetCoordinator(
				quantityUnitsContentState,
				onAddQuantityUnit = {
					addNewQuantityUnit(it)
				},
				onUpdateQuantityUnit = {
					// no-op on this screen
				},
			),
			onAddItem = { addNewItem(it) },
			onUpdateItem = { item, tagIds -> updateItem(item, tagIds) },
		),
	)

	/////////////////////////////////////////////////
	//  endregion
}
