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

class ManageLocationsContentCoordinator(
	override val locationsSourceFlow: StateFlow<LocationContentState>,
	override val createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
) : ManageLocationsContentCoordinatorImpl {
	override fun launchDataModelCreation() {
		createLocationCoordinator.showSheet()
	}

	override fun launchDataModelEdit(location: Location) {
		createLocationCoordinator.showSheetWithData(location)
	}
}

val stubManageLocationsContentCoordinator = ManageLocationsContentCoordinator(
	locationsSourceFlow = previewLocationContentFlow(),
	createLocationCoordinator = stubCreateLocationModalSheetCoordinator
)