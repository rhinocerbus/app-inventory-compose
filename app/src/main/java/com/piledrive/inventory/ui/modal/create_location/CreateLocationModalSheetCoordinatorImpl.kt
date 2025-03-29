package com.piledrive.inventory.ui.modal.create_location

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl


interface CreateLocationModalSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val onAddLocation: (slug: LocationSlug) -> Unit
}


/*
 todo - integrate show somehow, but with optional params
 - was considering adding to modalsheetcallbacks but that boxes-in or otherwise makes params a pain
 - class-level callback would work i guess
 -- might make modalsheetcallbacks pointless
 more to be seen when making a few versions of this per sheet
 */
class CreateLocationModalSheetCoordinator(
	override val onAddLocation: (slug: LocationSlug) -> Unit = {}
) : ModalSheetCoordinator(), CreateLocationModalSheetCoordinatorImpl

val stubCreateLocationModalSheetCoordinator = object : CreateLocationModalSheetCoordinatorImpl {
	override val onAddLocation: (slug: LocationSlug) -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)

	override fun showSheet() {
	}

	override fun onDismiss() {
	}
}