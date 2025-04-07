package com.piledrive.inventory.ui.screens.locations.content

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageLocationsContentCoordinatorImpl {
	val locationState: StateFlow<LocationContentState>
	val onLaunchCreateLocation: () -> Unit
	val onLocationClicked: (location: Location) -> Unit
}

val stubManageLocationsContentCoordinator = object : ManageLocationsContentCoordinatorImpl {
	override val locationState: StateFlow<LocationContentState> = previewLocationContentFlow()
	override val onLaunchCreateLocation: () -> Unit = {}
	override val onLocationClicked: (location: Location) -> Unit = {}
}

class ManageLocationsContentCoordinator(
	override val locationState: StateFlow<LocationContentState>,
	override val onLaunchCreateLocation: () -> Unit,
	override val onLocationClicked: (location: Location) -> Unit,
) : ManageLocationsContentCoordinatorImpl {
}