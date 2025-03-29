package com.piledrive.inventory.ui.modal.create_tag

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl


interface CreateTagSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val onAddTag: (slug: TagSlug) -> Unit
}

val stubCreateTagSheetCoordinator = object : CreateTagSheetCoordinatorImpl {
	override val onAddTag: (slug: TagSlug) -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)
	override fun showSheet() {}
	override fun onDismiss() {}
}

class CreateTagSheetCoordinator(
	override val onAddTag: (slug: TagSlug) -> Unit
) : ModalSheetCoordinator(), CreateTagSheetCoordinatorImpl