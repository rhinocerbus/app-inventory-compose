package com.piledrive.inventory.ui.modal.create_item

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewQuantityUnitsContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import kotlinx.coroutines.flow.StateFlow


interface CreateItemSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val activeItemState: State<ItemWithTags?>
	val itemState: StateFlow<ItemContentState>
	val quantityContentState: StateFlow<QuantityUnitContentState>
	val tagsContentState: StateFlow<TagsContentState>
	val onAddItem: (item: ItemSlug) -> Unit
	val onUpdateItem: (item: Item, tagIds: List<String>) -> Unit
	val onLaunchAddTag: () -> Unit
	val onLaunchAddUnit: () -> Unit
	fun showSheetForItem(item: ItemWithTags)
}

class CreateItemSheetCoordinator(
	override val itemState: StateFlow<ItemContentState>,
	override val quantityContentState: StateFlow<QuantityUnitContentState>,
	override val tagsContentState: StateFlow<TagsContentState>,
	override val onAddItem: (item: ItemSlug) -> Unit,
	override val onUpdateItem: (item: Item, tagIds: List<String>) -> Unit,
	override val onLaunchAddTag: () -> Unit,
	override val onLaunchAddUnit: () -> Unit
) : ModalSheetCoordinator(), CreateItemSheetCoordinatorImpl {

	private val _activeItemState: MutableState<ItemWithTags?> = mutableStateOf(null)
	override val activeItemState: State<ItemWithTags?> = _activeItemState

	override fun showSheetForItem(item: ItemWithTags) {
		_activeItemState.value = item
		_showSheetState.value = true
	}

	override fun showSheet() {
		_activeItemState.value = null
		super.showSheet()
	}
}

val stubCreateItemSheetCoordinator = object : CreateItemSheetCoordinatorImpl {
	override val activeItemState: State<ItemWithTags?> = mutableStateOf(null)
	override val itemState: StateFlow<ItemContentState> = previewItemsContentFlow()
	override val quantityContentState: StateFlow<QuantityUnitContentState> = previewQuantityUnitsContentFlow()
	override val tagsContentState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val onAddItem: (item: ItemSlug) -> Unit = {}
	override val onUpdateItem: (item: Item, tagIds: List<String>) -> Unit = { _, _ -> }
	override val onLaunchAddTag: () -> Unit = {}
	override val onLaunchAddUnit: () -> Unit = {}

	override val showSheetState: State<Boolean> = mutableStateOf(false)

	override fun showSheet() {
	}

	override fun showSheetForItem(item: ItemWithTags) {}

	override fun onDismiss() {
	}
}