package com.piledrive.inventory.ui.modal.create_tag

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import kotlinx.coroutines.flow.StateFlow


interface CreateTagSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val activeTagState: State<Tag?>
	val tagsContentState: StateFlow<TagsContentState>
	val onAddTag: (slug: TagSlug) -> Unit
	val onUpdateTag: (tag: Tag) -> Unit
	fun showSheetForTag(tag: Tag)
}

val stubCreateTagSheetCoordinator = object : CreateTagSheetCoordinatorImpl {
	override val activeTagState: State<Tag?> = mutableStateOf(null)
	override val tagsContentState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val onAddTag: (slug: TagSlug) -> Unit = {}
	override val onUpdateTag: (Tag) -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)
	override fun showSheet() {}
	override fun showSheetForTag(tag: Tag) {}
	override fun onDismiss() {}
}

class CreateTagSheetCoordinator(
	override val tagsContentState: StateFlow<TagsContentState>,
	override val onAddTag: (slug: TagSlug) -> Unit,
	override val onUpdateTag: (tag: Tag) -> Unit,
) : ModalSheetCoordinator(), CreateTagSheetCoordinatorImpl {

	private val _activeTagState: MutableState<Tag?> = mutableStateOf(null)
	override val activeTagState: State<Tag?> = _activeTagState

	override fun showSheetForTag(tag: Tag) {
		_activeTagState.value = tag
		_showSheetState.value = true
	}

	override fun showSheet() {
		_activeTagState.value = null
		super.showSheet()
	}
}