package com.piledrive.inventory.ui.modal.transfer_item

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.composite.StashForItemAtLocation
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric

interface TransferItemStashSheetCoordinator : ModalSheetCoordinatorImpl {
	val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation>
	val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation>

	val activeItemState: State<Item?>
	val optionPoolState: State<List<StashForItemAtLocation>>
	val fromLocationState: State<String?>
	val toLocationState: State<String?>
	val amountDifference: State<Double>

	val reloadOptions: (itemId: String) -> List<StashForItemAtLocation>
	val showSheetForItem: (forItem: Item) -> Unit
	val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit

	val onDismiss: () -> Unit
}

val stubTransferItemStashSheetCoordinator = object : TransferItemStashSheetCoordinator {
	override val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> =
		ReadOnlyDropdownCoordinatorGeneric()
	override val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> =
		ReadOnlyDropdownCoordinatorGeneric()
	override val activeItemState: State<Item?> = mutableStateOf(null)
	override val optionPoolState: State<List<StashForItemAtLocation>> = mutableStateOf(listOf())
	override val fromLocationState: State<String?> = mutableStateOf(null)
	override val toLocationState: State<String?> = mutableStateOf(null)
	override val amountDifference: State<Double> = mutableStateOf(0.0)
	override val reloadOptions: (itemId: String) -> List<StashForItemAtLocation> = { listOf() }
	override val showSheetForItem: (forItem: Item) -> Unit = {}
	override val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit = {_, _, _, _ ->}
	override val onDismiss: () -> Unit = {}
	override val showSheetState: State<Boolean> = mutableStateOf(false)
}

class TransferItemStashSheetCoordinator2(
	initialShowSheetValue: Boolean = false,
	initialItemValue: Item? = null,
	initialFromLocationValue: String? = null,
	initialToLocationValue: String? = null,
	initialAmountDifferenceValue: Double = 0.0,

	val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> = ReadOnlyDropdownCoordinatorGeneric(),
	val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> = ReadOnlyDropdownCoordinatorGeneric(),

	val reloadOptions: (itemId: String) -> List<StashForItemAtLocation> = { listOf() },

	val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit = { _, _, _, _ -> }
) : ModalSheetCoordinator(initialShowSheetValue) {
	private val _activeItemState: MutableState<Item?> = mutableStateOf(initialItemValue)
	val activeItemState: State<Item?> = _activeItemState
	private val _optionPoolState: MutableState<List<StashForItemAtLocation>> = mutableStateOf(listOf())
	val optionPoolState: State<List<StashForItemAtLocation>> = _optionPoolState
	private val _fromLocationState: MutableState<String?> = mutableStateOf(initialFromLocationValue)
	val fromLocationState: State<String?> = _fromLocationState
	private val _toLocationState: MutableState<String?> = mutableStateOf(initialToLocationValue)
	val toLocationState: State<String?> = _toLocationState
	private val _amountDifference: MutableState<Double> = mutableStateOf(initialAmountDifferenceValue)
	val amountDifference: State<Double> = _amountDifference

	fun showSheetForItem(forItem: Item) {
		_optionPoolState.value = reloadOptions(forItem.id)
		fromLocationDropdownCoordinator.udpateOptionsPool(_optionPoolState.value)
		toLocationDropdownCoordinator.udpateOptionsPool(_optionPoolState.value)

		_activeItemState.value = forItem
		_fromLocationState.value = null
		_toLocationState.value = null
		_amountDifference.value = 0.0

		_showSheetState.value = true
	}

	fun changeFromLocation(locId: String) {
		_fromLocationState.value = locId
	}

	fun changeToLocation(locId: String) {
		_toLocationState.value = locId
	}

	fun changeTransferAmount(amount: Double) {
		_amountDifference.value = amount
	}
}