package com.piledrive.inventory.ui.modal.create_item_stash

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.StashSlug
import com.piledrive.inventory.ui.modal.coordinators.CreateDataModalCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.stubCreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_location.stubCreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import kotlinx.coroutines.flow.StateFlow


interface CreateItemStashSheetCoordinatorImpl : ModalSheetCoordinatorImpl, CreateDataModalCoordinatorImpl<StashSlug> {
	val stashesSourceFlow: StateFlow<ItemStashContentState>
	val itemsSourceFlow: StateFlow<ItemContentState>
	val locationsSourceFlow: StateFlow<LocationContentState>
	val createItemCoordinator: CreateItemSheetCoordinatorImpl
	val createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl
	fun launchCreateItem() {
		createItemCoordinator.showSheet()
	}

	fun launchCreateLocation() {
		createLocationCoordinator.showSheet()
	}
}

class CreateItemStashSheetCoordinator(
	override val stashesSourceFlow: StateFlow<ItemStashContentState>,
	override val itemsSourceFlow: StateFlow<ItemContentState>,
	override val locationsSourceFlow: StateFlow<LocationContentState>,
	override val createItemCoordinator: CreateItemSheetCoordinatorImpl,
	override val createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
	override val onCreateDataModel: (slug: StashSlug) -> Unit,
) : ModalSheetCoordinator(), CreateItemStashSheetCoordinatorImpl {
}

val stubCreateItemStashSheetCoordinator = CreateItemStashSheetCoordinator(
	stashesSourceFlow = previewItemStashesContentFlow(),
	itemsSourceFlow = previewItemsContentFlow(),
	locationsSourceFlow = previewLocationContentFlow(listOf(Location(id = "", createdAt = "", name = "Pantry"))),
	createItemCoordinator = stubCreateItemSheetCoordinator,
	createLocationCoordinator = stubCreateLocationModalSheetCoordinator,
	onCreateDataModel = { },
)