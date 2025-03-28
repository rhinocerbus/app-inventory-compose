package com.piledrive.inventory.ui.modal.create_item

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import com.piledrive.lib_compose_components.ui.util.previewBooleanFlow


interface CreateItemSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val onAddItem: (item: ItemSlug) -> Unit
}

class CreateItemSheetCoordinator(
	override val onAddItem: (item: ItemSlug) -> Unit = { },
) : ModalSheetCoordinator(), CreateItemSheetCoordinatorImpl {
}

val stubCreateItemSheetCoordinator = object : CreateItemSheetCoordinatorImpl {
	override val onAddItem: (item: ItemSlug) -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)

	override fun showSheet() {
	}

	override fun onDismiss() {
	}
}