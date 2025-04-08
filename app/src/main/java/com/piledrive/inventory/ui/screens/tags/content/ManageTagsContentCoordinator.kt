package com.piledrive.inventory.ui.screens.tags.content

import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.stubCreateTagSheetCoordinator
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageTagsContentCoordinatorImpl {
	val tagState: StateFlow<TagsContentState>
	val onLaunchCreateTag: () -> Unit
	val onTagClicked: (tag: Tag) -> Unit
	val createTagCoordinator: CreateTagSheetCoordinatorImpl
}

val stubManageTagsContentCoordinator = object : ManageTagsContentCoordinatorImpl {
	override val tagState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val onLaunchCreateTag: () -> Unit = {}
	override val onTagClicked: (tag: Tag) -> Unit = {}
	override val createTagCoordinator: CreateTagSheetCoordinatorImpl = stubCreateTagSheetCoordinator
}

class ManageTagsContentCoordinator(
	override val createTagCoordinator: CreateTagSheetCoordinatorImpl,
	override val tagState: StateFlow<TagsContentState>,
) : ManageTagsContentCoordinatorImpl {
	override val onLaunchCreateTag: () -> Unit = {
		createTagCoordinator.showSheet()
	}
	override val onTagClicked: (tag: Tag) -> Unit = {
		createTagCoordinator.showSheetWithData(it)
	}
}
