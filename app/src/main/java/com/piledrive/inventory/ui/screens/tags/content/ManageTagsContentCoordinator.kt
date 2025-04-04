package com.piledrive.inventory.ui.screens.tags.content

import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageTagsContentCoordinatorImpl {
	val tagState: StateFlow<TagsContentState>
	val onLaunchCreateTag: () -> Unit
	val onTagClicked: (tag: Tag) -> Unit
}

val stubManageTagsContentCoordinator = object : ManageTagsContentCoordinatorImpl {
	override val tagState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val onLaunchCreateTag: () -> Unit = {}
	override val onTagClicked: (tag: Tag) -> Unit = {}
}

class ManageTagsContentCoordinator(
	override val tagState: StateFlow<TagsContentState>,
	override val onLaunchCreateTag: () -> Unit,
	override val onTagClicked: (tag: Tag) -> Unit,
) : ManageTagsContentCoordinatorImpl {
}