package com.piledrive.inventory.ui.screens.locations.content

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_location.stubCreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.screens.coordinators.ManageDataScreenImpl
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageLocationsContentCoordinatorImpl : ManageDataScreenImpl<Location> {
	val locationsSourceFlow: StateFlow<LocationContentState>
	val createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl
}

val stubManageLocationsContentCoordinator = object : ManageLocationsContentCoordinatorImpl {
	override val locationsSourceFlow: StateFlow<LocationContentState> = previewLocationContentFlow()
	override val createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl = stubCreateLocationModalSheetCoordinator
	override val onLaunchDataModelCreation: () -> Unit = {}
	override val onDataModelSelected: (location: Location) -> Unit = {}
}

class ManageLocationsContentCoordinator(
	override val locationsSourceFlow: StateFlow<LocationContentState>,
	override val createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
) : ManageLocationsContentCoordinatorImpl {
	override val onLaunchDataModelCreation: () -> Unit = {createLocationCoordinator.showSheet()}
	override val onDataModelSelected: (location: Location) -> Unit = { createLocationCoordinator.showSheetWithData(it) }
}