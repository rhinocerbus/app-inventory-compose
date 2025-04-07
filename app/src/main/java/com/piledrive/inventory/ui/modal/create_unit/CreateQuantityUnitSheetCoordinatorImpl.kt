package com.piledrive.inventory.ui.modal.create_unit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.QuantityType
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.util.previewQuantityUnitsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import kotlinx.coroutines.flow.StateFlow


interface CreateQuantityUnitSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val activeUnitState: State<QuantityUnit?>
	val selectedMeasurementState: State<QuantityType>
	val unitsContentState: StateFlow<QuantityUnitContentState>
	val onAddQuantityUnit: (slug: QuantityUnitSlug) -> Unit
	val onUpdateQuantityUnit: (unit: QuantityUnit) -> Unit
	fun changeSelectedType(type: QuantityType)
	fun showSheetForUnit(unit: QuantityUnit)
}

val stubCreateQuantityUnitSheetCoordinator = object : CreateQuantityUnitSheetCoordinatorImpl {
	override val activeUnitState: State<QuantityUnit?> = mutableStateOf(null)
	override val selectedMeasurementState: State<QuantityType> = mutableStateOf(QuantityType.WHOLE)
	override val unitsContentState: StateFlow<QuantityUnitContentState> = previewQuantityUnitsContentFlow()
	override val onAddQuantityUnit: (slug: QuantityUnitSlug) -> Unit = {}
	override val onUpdateQuantityUnit: (unit: QuantityUnit) -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)
	override fun changeSelectedType(type: QuantityType) {}
	override fun showSheet() {}
	override fun showSheetForUnit(unit: QuantityUnit) {}
	override fun onDismiss() {}
}

class CreateQuantityUnitSheetCoordinator(
	override val unitsContentState: StateFlow<QuantityUnitContentState>,
	override val onAddQuantityUnit: (slug: QuantityUnitSlug) -> Unit,
	override val onUpdateQuantityUnit: (unit: QuantityUnit) -> Unit
) : ModalSheetCoordinator(), CreateQuantityUnitSheetCoordinatorImpl {

	private val _selectedMeasurementState: MutableState<QuantityType> = mutableStateOf(QuantityType.WHOLE)
	override val selectedMeasurementState: State<QuantityType> = _selectedMeasurementState

	override fun changeSelectedType(type: QuantityType) {
		_selectedMeasurementState.value = type
	}

	private val _activeUnitState: MutableState<QuantityUnit?> = mutableStateOf(null)
	override val activeUnitState: State<QuantityUnit?> = _activeUnitState

	override fun showSheetForUnit(unit: QuantityUnit) {
		_activeUnitState.value = unit
		_showSheetState.value = true
	}

	override fun showSheet() {
		_activeUnitState.value = null
		super.showSheet()
	}
}
