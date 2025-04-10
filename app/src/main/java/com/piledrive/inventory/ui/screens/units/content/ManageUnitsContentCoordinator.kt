package com.piledrive.inventory.ui.screens.units.content

import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_unit.stubCreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.screens.coordinators.ManageDataScreenImpl
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.util.previewQuantityUnitsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface ManageUnitsContentCoordinatorImpl : ManageDataScreenImpl<QuantityUnit> {
	val unitsSourceFlow: StateFlow<QuantityUnitContentState>
	val createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl
}

class ManageUnitsContentCoordinator(
	override val unitsSourceFlow: StateFlow<QuantityUnitContentState>,
	override val createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
) : ManageUnitsContentCoordinatorImpl {
	override fun launchDataModelCreation() {
		createQuantityUnitSheetCoordinator.showSheet()
	}

	override fun launchDataModelEdit(unit: QuantityUnit) {
		createQuantityUnitSheetCoordinator.showSheetWithData(unit)
	}
}

val stubManageUnitsContentCoordinator = ManageUnitsContentCoordinator(
	unitsSourceFlow = previewQuantityUnitsContentFlow(),
	createQuantityUnitSheetCoordinator = stubCreateQuantityUnitSheetCoordinator
)
