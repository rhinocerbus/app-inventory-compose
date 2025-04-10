package com.piledrive.inventory.ui.screens.items.content

import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.stubCreateItemSheetCoordinator
import com.piledrive.inventory.ui.screens.coordinators.ManageDataScreenImpl
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.util.previewFullItemsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageItemsContentCoordinatorImpl : ManageDataScreenImpl<ItemWithTags> {
	val itemsSourceFlow: StateFlow<FullItemsContentState>
	val createItemCoordinator: CreateItemSheetCoordinatorImpl
}

class ManageItemsContentCoordinator(
	override val itemsSourceFlow: StateFlow<FullItemsContentState>,
	override val createItemCoordinator: CreateItemSheetCoordinatorImpl,
) : ManageItemsContentCoordinatorImpl {
	override fun launchDataModelCreation() {
		createItemCoordinator.showSheet()
	}

	override fun launchDataModelEdit(item: ItemWithTags) {
		createItemCoordinator.showSheetWithData(item)
	}
}

val stubManageItemsContentCoordinator = ManageItemsContentCoordinator(
	itemsSourceFlow = previewFullItemsContentFlow(),
	createItemCoordinator = stubCreateItemSheetCoordinator
) 