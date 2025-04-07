package com.piledrive.inventory.ui.screens.items.content

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.util.previewFullItemsContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageItemsContentCoordinatorImpl {
	val itemState: StateFlow<FullItemsContentState>
	val onLaunchCreateItem: () -> Unit
	val onItemClicked: (item: ItemWithTags) -> Unit
}

val stubManageItemsContentCoordinator = object : ManageItemsContentCoordinatorImpl {
	override val itemState: StateFlow<FullItemsContentState> = previewFullItemsContentFlow()
	override val onLaunchCreateItem: () -> Unit = {}
	override val onItemClicked: (item: ItemWithTags) -> Unit = {}
}

class ManageItemsContentCoordinator(
	override val itemState: StateFlow<FullItemsContentState>,
	override val onLaunchCreateItem: () -> Unit,
	override val onItemClicked: (item: ItemWithTags) -> Unit,
) : ManageItemsContentCoordinatorImpl {
}