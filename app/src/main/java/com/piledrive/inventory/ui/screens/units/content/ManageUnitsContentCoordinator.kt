package com.piledrive.inventory.ui.screens.units.content

import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_unit.stubCreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.screens.coordinators.ManageDataScreenImpl
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.util.previewUnitsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageUnitsContentCoordinatorImpl : ManageDataScreenImpl<QuantityUnit> {
	val unitState: StateFlow<QuantityUnitContentState>
	val createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl
}

val stubManageUnitsContentCoordinator = object : ManageUnitsContentCoordinatorImpl {
	override val unitState: StateFlow<QuantityUnitContentState> = previewUnitsContentFlow()
	override val createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl =
		stubCreateQuantityUnitSheetCoordinator
	override val onLaunchDataModelCreation: () -> Unit = {}
	override val onDataModelSelected: (unit: QuantityUnit) -> Unit = {}
}

class ManageUnitsContentCoordinator(
	override val unitState: StateFlow<QuantityUnitContentState>,
	override val createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
) : ManageUnitsContentCoordinatorImpl {
	override val onLaunchDataModelCreation: () -> Unit = { createQuantityUnitSheetCoordinator.showSheet() }
	override val onDataModelSelected: (unit: QuantityUnit) -> Unit = { createQuantityUnitSheetCoordinator.showSheetWithData(it) }
}