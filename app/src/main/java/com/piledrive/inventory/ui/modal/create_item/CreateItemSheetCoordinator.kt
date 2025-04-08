package com.piledrive.inventory.ui.modal.create_item

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.ui.modal.coordinators.EditableDataModalCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.stubCreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_unit.stubCreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewQuantityUnitsContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import kotlinx.coroutines.flow.StateFlow


interface CreateItemSheetCoordinatorImpl : ModalSheetCoordinatorImpl, EditableDataModalCoordinatorImpl<ItemWithTags> {
	val itemState: StateFlow<ItemContentState>
	val quantityContentState: StateFlow<QuantityUnitContentState>
	val tagsContentState: StateFlow<TagsContentState>
	val createTagCoordinator: CreateTagSheetCoordinatorImpl
	val createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl
	val onAddItem: (item: ItemSlug) -> Unit
	val onUpdateItem: (item: Item, tagIds: List<String>) -> Unit
	val onLaunchAddTag: () -> Unit
	val onLaunchAddUnit: () -> Unit
}

class CreateItemSheetCoordinator(
	override val itemState: StateFlow<ItemContentState>,
	override val quantityContentState: StateFlow<QuantityUnitContentState>,
	override val tagsContentState: StateFlow<TagsContentState>,
	override val createTagCoordinator: CreateTagSheetCoordinatorImpl,
	override val createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
	override val onAddItem: (item: ItemSlug) -> Unit,
	override val onUpdateItem: (item: Item, tagIds: List<String>) -> Unit,
) : ModalSheetCoordinator(), CreateItemSheetCoordinatorImpl {
	override val onLaunchAddTag: () -> Unit = { createTagCoordinator.showSheet() }
	override val onLaunchAddUnit: () -> Unit = { createQuantityUnitSheetCoordinator.showSheet() }

	private val _activeEditDataState: MutableState<ItemWithTags?> = mutableStateOf(null)
	override val activeEditDataState: State<ItemWithTags?> = _activeEditDataState

	override fun showSheetWithData(item: ItemWithTags) {
		_activeEditDataState.value = item
		_showSheetState.value = true
	}

	override fun showSheet() {
		_activeEditDataState.value = null
		super.showSheet()
	}
}

val stubCreateItemSheetCoordinator = object : CreateItemSheetCoordinatorImpl {
	override val activeEditDataState: State<ItemWithTags?> = mutableStateOf(null)
	override val itemState: StateFlow<ItemContentState> = previewItemsContentFlow()
	override val quantityContentState: StateFlow<QuantityUnitContentState> = previewQuantityUnitsContentFlow()
	override val tagsContentState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val createTagCoordinator: CreateTagSheetCoordinatorImpl = stubCreateTagSheetCoordinator
	override val createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl =
		stubCreateQuantityUnitSheetCoordinator
	override val onAddItem: (item: ItemSlug) -> Unit = {}
	override val onUpdateItem: (item: Item, tagIds: List<String>) -> Unit = { _, _ -> }
	override val onLaunchAddTag: () -> Unit = {}
	override val onLaunchAddUnit: () -> Unit = {}

	override val showSheetState: State<Boolean> = mutableStateOf(false)

	override fun showSheet() {
	}

	override fun showSheetWithData(item: ItemWithTags) {}

	override fun onDismiss() {
	}
}