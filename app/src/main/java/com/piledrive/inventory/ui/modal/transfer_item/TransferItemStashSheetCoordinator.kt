package com.piledrive.inventory.ui.modal.transfer_item

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.composite.StashForItemAtLocation
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric

interface TransferItemStashSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation>
	val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation>

	val activeItemState: State<Item?>
	//val fromLocationState: State<String?>
	//val toLocationState: State<String?>
	val amountDifference: State<Double>

	val reloadFromOptions: (itemId: String) -> List<StashForItemAtLocation>
	val reloadToOptions: (itemId: String) -> List<StashForItemAtLocation>

	val showSheetForItem: (forItem: Item) -> Unit
	val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit

	val onDismiss: () -> Unit
}

val stubTransferItemStashSheetCoordinator = object : TransferItemStashSheetCoordinatorImpl {
	override val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> =
		ReadOnlyDropdownCoordinatorGeneric()
	override val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> =
		ReadOnlyDropdownCoordinatorGeneric()

	override val activeItemState: State<Item?> = mutableStateOf(null)
	//override val fromLocationState: State<String?> = mutableStateOf(null)
	//override val toLocationState: State<String?> = mutableStateOf(null)
	override val amountDifference: State<Double> = mutableStateOf(0.0)

	override val reloadFromOptions: (itemId: String) -> List<StashForItemAtLocation> = { listOf() }
	override val reloadToOptions: (itemId: String) -> List<StashForItemAtLocation> = { listOf() }

	override val showSheetForItem: (forItem: Item) -> Unit = {}
	override val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit = {_, _, _, _ ->}
	override val onDismiss: () -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)
}

class TransferItemStashSheetCoordinator(
	initialShowSheetValue: Boolean = false,
	initialItemValue: Item? = null,
	//initialFromLocationValue: String? = null,
	//initialToLocationValue: String? = null,
	initialAmountDifferenceValue: Double = 0.0,

	override val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> = ReadOnlyDropdownCoordinatorGeneric(),
	override val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> = ReadOnlyDropdownCoordinatorGeneric(),

	// could have "source" data states/flows instead of an external mutation if we want to keep the logic private
	override val reloadFromOptions: (itemId: String) -> List<StashForItemAtLocation> = { listOf() },
	override val reloadToOptions: (itemId: String) -> List<StashForItemAtLocation> = { listOf() },

	override val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit = { _, _, _, _ -> }
) : TransferItemStashSheetCoordinatorImpl {
	private val _showSheetState: MutableState<Boolean> = mutableStateOf(initialShowSheetValue)
	override val showSheetState: State<Boolean> = _showSheetState

	private val _activeItemState: MutableState<Item?> = mutableStateOf(initialItemValue)
	override val activeItemState: State<Item?> = _activeItemState
	//private val _fromLocationState: MutableState<String?> = mutableStateOf(initialFromLocationValue)
	//override val fromLocationState: State<String?> = _fromLocationState
	//private val _toLocationState: MutableState<String?> = mutableStateOf(initialToLocationValue)
	//override val toLocationState: State<String?> = _toLocationState
	private val _amountDifference: MutableState<Double> = mutableStateOf(initialAmountDifferenceValue)
	override val amountDifference: State<Double> = _amountDifference

	override val showSheetForItem: (forItem: Item) -> Unit = { forItem ->
		val fromOptions = reloadFromOptions(forItem.id)
		fromLocationDropdownCoordinator.udpateOptionsPool(fromOptions)

		val toOptions = reloadToOptions(forItem.id)
		toLocationDropdownCoordinator.udpateOptionsPool(toOptions)

		_activeItemState.value = forItem
		fromLocationDropdownCoordinator.onOptionSelected(null)
		toLocationDropdownCoordinator.onOptionSelected(null)
		//_fromLocationState.value = null
		//_toLocationState.value = null
		_amountDifference.value = 0.0

		_showSheetState.value = true
	}
/*
	fun changeFromLocation(locId: String) {
		//_fromLocationState.value = locId
		fromLocationDropdownCoordinator.onOptionSelected(locId)
	}

	fun changeToLocation(locId: String) {
		//_toLocationState.value = locId
		toLocationDropdownCoordinator.onOptionSelected(locId)
	}
*/

	fun changeTransferAmount(amount: Double) {
		_amountDifference.value = amount
	}

	override val onDismiss: () -> Unit = {
		_showSheetState.value = false
	}
}