package com.piledrive.inventory.ui.modal.create_location

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.ui.modal.coordinators.EditableDataModalCoordinatorImpl
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import kotlinx.coroutines.flow.StateFlow


interface CreateLocationModalSheetCoordinatorImpl : ModalSheetCoordinatorImpl,
	EditableDataModalCoordinatorImpl<Location, LocationSlug> {
	val locationsSourceFlow: StateFlow<LocationContentState>
}


/*
 todo - integrate show somehow, but with optional params
 - was considering adding to modalsheetcallbacks but that boxes-in or otherwise makes params a pain
 - class-level callback would work i guess
 -- might make modalsheetcallbacks pointless
 more to be seen when making a few versions of this per sheet
 */
class CreateLocationModalSheetCoordinator(
	override val locationsSourceFlow: StateFlow<LocationContentState>,
	override val onCreateDataModel: (slug: LocationSlug) -> Unit,
	override val onUpdateDataModel: (location: Location) -> Unit,
) : ModalSheetCoordinator(), CreateLocationModalSheetCoordinatorImpl {

	private val _activeEditDataState: MutableState<Location?> = mutableStateOf(null)
	override val activeEditDataState: State<Location?> = _activeEditDataState

	override fun showSheetWithData(location: Location) {
		_activeEditDataState.value = location
		_showSheetState.value = true
	}

	override fun showSheet() {
		_activeEditDataState.value = null
		super.showSheet()
	}
}

val stubCreateLocationModalSheetCoordinator = CreateLocationModalSheetCoordinator(
	locationsSourceFlow = previewLocationContentFlow(),
	onCreateDataModel = {},
	onUpdateDataModel = {}
)
