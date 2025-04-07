package com.piledrive.inventory.ui.screens.units.content

import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.util.previewUnitsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageUnitsContentCoordinatorImpl {
	val unitState: StateFlow<QuantityUnitContentState>
	val onLaunchCreateUnit: () -> Unit
	val onUnitClicked: (unit: QuantityUnit) -> Unit
}

val stubManageUnitsContentCoordinator = object : ManageUnitsContentCoordinatorImpl {
	override val unitState: StateFlow<QuantityUnitContentState> = previewUnitsContentFlow()
	override val onLaunchCreateUnit: () -> Unit = {}
	override val onUnitClicked: (unit: QuantityUnit) -> Unit = {}
}

class ManageUnitsContentCoordinator(
	override val unitState: StateFlow<QuantityUnitContentState>,
	override val onLaunchCreateUnit: () -> Unit,
	override val onUnitClicked: (unit: QuantityUnit) -> Unit,
) : ManageUnitsContentCoordinatorImpl {
}