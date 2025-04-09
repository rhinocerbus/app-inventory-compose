package com.piledrive.inventory.ui.screens.tags.content

import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.stubCreateTagSheetCoordinator
import com.piledrive.inventory.ui.screens.coordinators.ManageDataScreenImpl
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageTagsContentCoordinatorImpl : ManageDataScreenImpl<Tag> {
	val tagState: StateFlow<TagsContentState>
	val createTagCoordinator: CreateTagSheetCoordinatorImpl
}

val stubManageTagsContentCoordinator = object : ManageTagsContentCoordinatorImpl {
	override val tagState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val onLaunchDataModelCreation: () -> Unit = {}
	override val onDataModelSelected: (tag: Tag) -> Unit = {}
	override val createTagCoordinator: CreateTagSheetCoordinatorImpl = stubCreateTagSheetCoordinator
}

class ManageTagsContentCoordinator(
	override val createTagCoordinator: CreateTagSheetCoordinatorImpl,
	override val tagState: StateFlow<TagsContentState>,
) : ManageTagsContentCoordinatorImpl {
	override val onLaunchDataModelCreation: () -> Unit = {
		createTagCoordinator.showSheet()
	}
	override val onDataModelSelected: (tag: Tag) -> Unit = {
		createTagCoordinator.showSheetWithData(it)
	}
}
