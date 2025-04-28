package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.enums.SortOrder
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.data.model.composite.FullItemData
import com.piledrive.inventory.data.model.composite.FullItemsContent
import com.piledrive.inventory.repo.Item2TagsRepo
import com.piledrive.inventory.repo.ItemsRepo
import com.piledrive.inventory.repo.QuantityUnitsRepo
import com.piledrive.inventory.repo.TagsRepo
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.screens.items.content.ManageItemsContentCoordinator
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.viewmodel.nuggets.ItemsCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.merge
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

	/* not init block because instantiation vs declaration order
	init {
		initDataSync()
	}
	*/

	fun initDataSync() {
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
							initWatches()
						}
					}
				}
			}
		}
	}

	// todo - can clean this up further
	private fun initWatches() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				val fullItemsSource = itemsDataCollector.fullItemsContentFlow
				merge(fullItemsSource)
					.debounce(500)
					.collect {
						rebuildItemsWithTags()
					}
			}
		}
	}

	//  region Item data
	/////////////////////////////////////////////////

	private val itemsDataCollector = ItemsCollector(
		viewModelScope,
		itemsRepo.watchItems(),
		quantityUnitsRepo.watchQuantityUnits(),
		tagsRepo.watchTags(),
		item2TagsRepo.watchItem2Tags()
	)

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

	private fun addNewTag(slug: TagSlug) {
		viewModelScope.launch {
			tagsRepo.addTag(slug)
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
		val fullItems = itemsDataCollector.fullItemsContentFlow.value.data.fullItems

		val sort = SortOrder.DEFAULT
		val sortDesc = true
		val sorted = when (sort) {
			SortOrder.NAME -> {
				if (sortDesc) {
					fullItems.sortedByDescending { it.item.name }
				} else {
					fullItems.sortedBy { it.item.name }
				}
			}

			SortOrder.LAST_ADDED -> {
				if (sortDesc) {
					fullItems.sortedByDescending { it.item.createdAt }
				} else {
					fullItems.sortedBy { it.item.createdAt }
				}
			}

			SortOrder.LAST_UPDATED -> {
				if (sortDesc) {
					fullItems.sortedByDescending { it.item.createdAt }
				} else {
					fullItems.sortedBy { it.item.createdAt }
				}
			}
		}

		val content = FullItemsContent(
			sorted
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


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val contentCoordinator = ManageItemsContentCoordinator(
		itemsSourceFlow = fullItemsContentFlow,
		createItemCoordinator = CreateItemSheetCoordinator(
			itemsSourceFlow = itemsDataCollector.itemsContentFlow,
			unitsSourceFlow = itemsDataCollector.quantityUnitsContentFlow,
			tagsSourceFlow = itemsDataCollector.userTagsContentFlow,
			createTagCoordinator = CreateTagSheetCoordinator(
				tagsSourceFlow = itemsDataCollector.userTagsContentFlow,
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
				unitsSourceFlow = itemsDataCollector.quantityUnitsContentFlow,
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
