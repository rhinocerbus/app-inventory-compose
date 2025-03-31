package com.piledrive.inventory.ui.modal.create_item

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.ItemSlug
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
	val itemState: StateFlow<ItemContentState>
	val quantityContentState: StateFlow<QuantityUnitContentState>
	val tagsContentState: StateFlow<TagsContentState>
	val onAddItem: (item: ItemSlug) -> Unit
	val onLaunchAddTag: () -> Unit
	val onLaunchAddUnit: () -> Unit
}

class CreateItemSheetCoordinator(
	override val itemState: StateFlow<ItemContentState>,
	override val quantityContentState: StateFlow<QuantityUnitContentState>,
	override val tagsContentState: StateFlow<TagsContentState>,
	override val onAddItem: (item: ItemSlug) -> Unit,
	override val onLaunchAddTag: () -> Unit,
	override val onLaunchAddUnit: () -> Unit
) : ModalSheetCoordinator(), CreateItemSheetCoordinatorImpl

val stubCreateItemSheetCoordinator = object : CreateItemSheetCoordinatorImpl {
	override val itemState: StateFlow<ItemContentState> = previewItemsContentFlow()
	override val quantityContentState: StateFlow<QuantityUnitContentState> = previewQuantityUnitsContentFlow()
	override val tagsContentState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val onAddItem: (item: ItemSlug) -> Unit = {}
	override val onLaunchAddTag: () -> Unit = {}
	override val onLaunchAddUnit: () -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)

	override fun showSheet() {
	}

	override fun onDismiss() {
	}
}