package com.piledrive.inventory.ui.modal.transfer_item

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.composite.StashForItemAtLocation
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import kotlinx.coroutines.flow.StateFlow


class TransferItemStashSheetCoordinator(
	initialShowSheetValue: Boolean = false,
	initialItemValue: Item? = null,
	initialFromLocationValue: String? = null,
	initialToLocationValue: String? = null,
	initialAmountDifferenceValue: Double = 0.0,

	val stashesStateSource: StateFlow<ItemStashContentState> = previewItemStashesContentFlow(),
	val itemStateSource: StateFlow<ItemContentState> = previewItemsContentFlow(),
	val locationsStateSource: StateFlow<LocationContentState> = previewLocationContentFlow(),

	val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location> = ReadOnlyDropdownCoordinatorGeneric(),
	val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location> = ReadOnlyDropdownCoordinatorGeneric(),

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