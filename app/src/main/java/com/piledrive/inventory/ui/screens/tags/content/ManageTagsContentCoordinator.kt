package com.piledrive.inventory.ui.screens.tags.content

import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.stubCreateTagSheetCoordinator
import com.piledrive.inventory.ui.screens.coordinators.ManageDataScreenImpl
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageTagsContentCoordinatorImpl : ManageDataScreenImpl<Tag> {
	val tagsSourceFlow: StateFlow<TagsContentState>
	val createTagCoordinator: CreateTagSheetCoordinatorImpl
}

class ManageTagsContentCoordinator(
	override val createTagCoordinator: CreateTagSheetCoordinatorImpl,
	override val tagsSourceFlow: StateFlow<TagsContentState>,
) : ManageTagsContentCoordinatorImpl {
	override fun launchDataModelCreation() {
		createTagCoordinator.showSheet()
	}

	override fun launchDataModelEdit(tag: Tag) {
		createTagCoordinator.showSheetWithData(tag)
	}
}

val stubManageTagsContentCoordinator = ManageTagsContentCoordinator(
	tagsSourceFlow = previewTagsContentFlow(),
	createTagCoordinator = stubCreateTagSheetCoordinator
)
