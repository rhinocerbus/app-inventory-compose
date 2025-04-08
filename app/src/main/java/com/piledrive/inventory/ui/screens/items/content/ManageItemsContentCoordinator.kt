package com.piledrive.inventory.ui.screens.items.content

import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.stubCreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.stubCreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_unit.stubCreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.util.previewFullItemsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageItemsContentCoordinatorImpl {
	val itemState: StateFlow<FullItemsContentState>
	val createItemCoordinator: CreateItemSheetCoordinatorImpl
	val onLaunchCreateItem: () -> Unit
	val onItemClicked: (item: ItemWithTags) -> Unit
}

val stubManageItemsContentCoordinator = object : ManageItemsContentCoordinatorImpl {
	override val itemState: StateFlow<FullItemsContentState> = previewFullItemsContentFlow()
	override val createItemCoordinator: CreateItemSheetCoordinatorImpl = stubCreateItemSheetCoordinator
	override val onLaunchCreateItem: () -> Unit = {}
	override val onItemClicked: (item: ItemWithTags) -> Unit = {}
}

class ManageItemsContentCoordinator(
	override val itemState: StateFlow<FullItemsContentState>,
	override val createItemCoordinator: CreateItemSheetCoordinatorImpl,
) : ManageItemsContentCoordinatorImpl {
	override val onLaunchCreateItem: () -> Unit = { createItemCoordinator.showSheet() }
	override val onItemClicked: (item: ItemWithTags) -> Unit = { createItemCoordinator.showSheetWithData(it) }
}