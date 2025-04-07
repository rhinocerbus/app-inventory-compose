package com.piledrive.inventory.ui.modal.create_location

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.data.model.composite.StashForItem
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import kotlinx.coroutines.flow.StateFlow


interface CreateLocationModalSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val activeLocationState: State<Location?>
	val locationState: StateFlow<LocationContentState>
	val onAddLocation: (slug: LocationSlug) -> Unit
	val onUpdateLocation: (location: Location) -> Unit
	fun showSheetForLocation(location: Location)
}


/*
 todo - integrate show somehow, but with optional params
 - was considering adding to modalsheetcallbacks but that boxes-in or otherwise makes params a pain
 - class-level callback would work i guess
 -- might make modalsheetcallbacks pointless
 more to be seen when making a few versions of this per sheet
 */
class CreateLocationModalSheetCoordinator(
	override val locationState: StateFlow<LocationContentState>,
	override val onAddLocation: (slug: LocationSlug) -> Unit,
	override val onUpdateLocation: (location: Location) -> Unit,
) : ModalSheetCoordinator(), CreateLocationModalSheetCoordinatorImpl {

	private val _activeLocationState: MutableState<Location?> = mutableStateOf(null)
	override val activeLocationState: State<Location?> = _activeLocationState

	override fun showSheetForLocation(location: Location) {
		_activeLocationState.value = location
		_showSheetState.value = true
	}

	override fun showSheet() {
		_activeLocationState.value = null
		super.showSheet()
	}
}

val stubCreateLocationModalSheetCoordinator = object : CreateLocationModalSheetCoordinatorImpl {
	override val activeLocationState: State<Location?> = mutableStateOf(null)
	override val locationState: StateFlow<LocationContentState> = previewLocationContentFlow()
	override val onAddLocation: (slug: LocationSlug) -> Unit = {}
	override val onUpdateLocation: (location: Location) -> Unit = {}

	override val showSheetState: State<Boolean> = mutableStateOf(false)

	override fun showSheet() {
	}

	override fun showSheetForLocation(location: Location) {
	}

	override fun onDismiss() {
	}
}