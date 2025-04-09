package com.piledrive.inventory.ui.modal.create_tag

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.ui.modal.coordinators.EditableDataModalCoordinatorImpl
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import kotlinx.coroutines.flow.StateFlow


interface CreateTagSheetCoordinatorImpl : ModalSheetCoordinatorImpl, EditableDataModalCoordinatorImpl<Tag, TagSlug> {
	val tagsSourceFlow: StateFlow<TagsContentState>
}

val stubCreateTagSheetCoordinator = object : CreateTagSheetCoordinatorImpl {
	override val activeEditDataState: State<Tag?> = mutableStateOf(null)
	override val tagsSourceFlow: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val onCreateDataModel: (slug: TagSlug) -> Unit = {}
	override val onUpdateDataModel: (Tag) -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)
	override fun showSheet() {}
	override fun showSheetWithData(tag: Tag) {}
	override fun onDismiss() {}
}

class CreateTagSheetCoordinator(
	override val tagsSourceFlow: StateFlow<TagsContentState>,
	override val onCreateDataModel: (slug: TagSlug) -> Unit,
	override val onUpdateDataModel: (tag: Tag) -> Unit,
) : ModalSheetCoordinator(), CreateTagSheetCoordinatorImpl {

	private val _activeEditDataState: MutableState<Tag?> = mutableStateOf(null)
	override val activeEditDataState: State<Tag?> = _activeEditDataState

	override fun showSheetWithData(tag: Tag) {
		_activeEditDataState.value = tag
		_showSheetState.value = true
	}

	override fun showSheet() {
		_activeEditDataState.value = null
		super.showSheet()
	}
}