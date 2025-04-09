package com.piledrive.inventory.ui.screens.items.content

import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.stubCreateItemSheetCoordinator
import com.piledrive.inventory.ui.screens.coordinators.ManageDataScreenImpl
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.util.previewFullItemsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageItemsContentCoordinatorImpl : ManageDataScreenImpl<ItemWithTags> {
	val itemState: StateFlow<FullItemsContentState>
	val createItemCoordinator: CreateItemSheetCoordinatorImpl
}

val stubManageItemsContentCoordinator = object : ManageItemsContentCoordinatorImpl {
	override val itemState: StateFlow<FullItemsContentState> = previewFullItemsContentFlow()
	override val createItemCoordinator: CreateItemSheetCoordinatorImpl = stubCreateItemSheetCoordinator
	override val onLaunchDataModelCreation: () -> Unit = {}
	override val onDataModelSelected: (item: ItemWithTags) -> Unit = {}
}

class ManageItemsContentCoordinator(
	override val itemState: StateFlow<FullItemsContentState>,
	override val createItemCoordinator: CreateItemSheetCoordinatorImpl,
) : ManageItemsContentCoordinatorImpl {
	override val onLaunchDataModelCreation: () -> Unit = { createItemCoordinator.showSheet() }
	override val onDataModelSelected: (item: ItemWithTags) -> Unit = { createItemCoordinator.showSheetWithData(it) }
}