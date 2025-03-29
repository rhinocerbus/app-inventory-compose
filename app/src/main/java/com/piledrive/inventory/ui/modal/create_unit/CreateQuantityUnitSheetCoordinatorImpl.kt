package com.piledrive.inventory.ui.modal.create_unit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.QuantityType
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl


interface CreateQuantityUnitSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val onAddQuantityUnit: (slug: QuantityUnitSlug) -> Unit
	val selectedMeasurement: MutableState<QuantityType>
}

val stubCreateQuantityUnitSheetCoordinator = object : CreateQuantityUnitSheetCoordinatorImpl {
	override val onAddQuantityUnit: (slug: QuantityUnitSlug) -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)
	override val selectedMeasurement: MutableState<QuantityType> = mutableStateOf(QuantityType.WHOLE)
	override fun showSheet() {}
	override fun onDismiss() {}
}

class CreateQuantityUnitSheetCoordinator(
	override val selectedMeasurement: MutableState<QuantityType> = mutableStateOf(QuantityType.WHOLE),
	override val onAddQuantityUnit: (slug: QuantityUnitSlug) -> Unit
) : ModalSheetCoordinator(), CreateQuantityUnitSheetCoordinatorImpl
