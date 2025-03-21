package com.piledrive.inventory.ui.modal.transfer_item

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.STATIC_ID_NEW_FROM_TRANSFER
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.composite.StashForItemAtLocation
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewQuantityUnitsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import kotlinx.coroutines.flow.StateFlow

interface TransferItemStashSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation>
	val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation>

	val activeItemState: State<Item?>
	val amountDifference: State<Double>
	val modifiedAmount: State<Double>

	// going back to data sources since any changes to data loading being external is annoying and feels messy
	val stashesSource: StateFlow<ItemStashContentState>
	val itemsSource: StateFlow<ItemContentState>
	val locationsSource: StateFlow<LocationContentState>
	val unitsSource: StateFlow<QuantityUnitContentState>

	val showSheetForItem: (forItem: Item) -> Unit
	val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit

	val onDismiss: () -> Unit
	fun changeTransferAmount(amount: Double)
	fun submitTransfer()
}

val stubTransferItemStashSheetCoordinator = object : TransferItemStashSheetCoordinatorImpl {
	override val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> =
		ReadOnlyDropdownCoordinatorGeneric()
	override val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> =
		ReadOnlyDropdownCoordinatorGeneric()

	override val activeItemState: State<Item?> = mutableStateOf(null)

	override val amountDifference: State<Double> = mutableDoubleStateOf(0.0)
	override val modifiedAmount: State<Double> = mutableDoubleStateOf(-1.0)

	override val stashesSource: StateFlow<ItemStashContentState> = previewItemStashesContentFlow()
	override val itemsSource: StateFlow<ItemContentState> = previewItemsContentFlow()
	override val locationsSource: StateFlow<LocationContentState> = previewLocationContentFlow()
	override val unitsSource: StateFlow<QuantityUnitContentState> = previewQuantityUnitsContentFlow()

	override val showSheetForItem: (forItem: Item) -> Unit = {}
	override val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit =
		{ _, _, _, _ -> }
	override val onDismiss: () -> Unit = {}

	override fun changeTransferAmount(amount: Double) {}
	override fun submitTransfer() {}

	override val showSheetState: State<Boolean> = mutableStateOf(false)
}

class TransferItemStashSheetCoordinator(
	initialShowSheetValue: Boolean = false,
	initialItemValue: Item? = null,
	initialAmountDifferenceValue: Double = 0.0,
	initialModifiedAmountValue: Double = -1.0,

	override val itemsSource: StateFlow<ItemContentState>,
	override val unitsSource: StateFlow<QuantityUnitContentState>,
	override val locationsSource: StateFlow<LocationContentState>,
	override val stashesSource: StateFlow<ItemStashContentState>,

	override val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit = { _, _, _, _ -> }
) : TransferItemStashSheetCoordinatorImpl {
	private val _showSheetState: MutableState<Boolean> = mutableStateOf(initialShowSheetValue)
	override val showSheetState: State<Boolean> = _showSheetState

	private val _activeItemState: MutableState<Item?> = mutableStateOf(initialItemValue)

	override val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> =
		ReadOnlyDropdownCoordinatorGeneric(
			externalOnOptionSelected = {
				_modifiedAmount.value = 0.0
				_amountDifference.value = 0.0
			}
		)
	override val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<StashForItemAtLocation> =
		ReadOnlyDropdownCoordinatorGeneric()


	override val activeItemState: State<Item?> = _activeItemState

	private val _amountDifference: MutableState<Double> = mutableDoubleStateOf(initialAmountDifferenceValue)
	override val amountDifference: State<Double> = _amountDifference
	private val _modifiedAmount: MutableState<Double> = mutableDoubleStateOf(initialModifiedAmountValue)
	override val modifiedAmount: State<Double> = _modifiedAmount

	override val showSheetForItem: (forItem: Item) -> Unit = { forItem ->
		reload(forItem.id)

		_activeItemState.value = forItem
		fromLocationDropdownCoordinator.onOptionSelected(null)
		toLocationDropdownCoordinator.onOptionSelected(null)
		_amountDifference.value = 0.0

		_showSheetState.value = true
	}

	private fun reload(itemId: String) {
		val items = itemsSource.value.data.items
		val quantityUnits = unitsSource.value.data.allUnits
		val stashes = stashesSource.value.data.itemStashes
		val locations = locationsSource.value.data.userLocations

		val rootItem = items.firstOrNull { it.id == itemId } ?: throw IllegalStateException("no item by given id")
		val withUnit = quantityUnits.firstOrNull { it.id == rootItem.unitId } ?: throw IllegalStateException("no unit for item")
		val stashesForItem = stashes.filter { it.itemId == itemId }

		val fromStashes = mutableListOf<StashForItemAtLocation>()
		stashesForItem.forEach { stash ->
			if (stash.amount == 0.0) return@forEach
			val atLocation = locations.firstOrNull { it.id == stash.locationId } ?: run { return@forEach }
			fromStashes.add(
				StashForItemAtLocation(
					stash = stash,
					location = atLocation,
					item = rootItem,
					quantityUnit = withUnit
				)
			)
		}
		fromLocationDropdownCoordinator.udpateOptionsPool(fromStashes)

		val toStashes = mutableListOf<StashForItemAtLocation>()
		locations.forEach { location ->
			val stashForLoc = stashesForItem.firstOrNull { it.locationId == location.id }
			toStashes.add(
				StashForItemAtLocation(
					stash = stashForLoc ?: Stash(STATIC_ID_NEW_FROM_TRANSFER, "", rootItem.id, location.id, 0.0),
					location = location,
					item = rootItem,
					quantityUnit = withUnit
				)
			)
		}
		toLocationDropdownCoordinator.udpateOptionsPool(toStashes)
	}

	override fun changeTransferAmount(amount: Double) {
		val startAmount = fromLocationDropdownCoordinator.selectedOptionState.value?.stash?.amount ?: return
		val clampedAmount = clampDouble(amount, 0.0, startAmount)
		_amountDifference.value = clampedAmount
		val endAmount = startAmount - clampedAmount
		_modifiedAmount.value = endAmount
	}

	override fun submitTransfer() {
		val fromStash = fromLocationDropdownCoordinator.selectedOptionState.value?.stash ?: throw IllegalStateException("missing data for transfer: from stash")
		val toStash = toLocationDropdownCoordinator.selectedOptionState.value?.stash ?: throw IllegalStateException("missing data for transfer: from stash")
		val updatedFromAmount = fromStash.amount - amountDifference.value
		val updatedToAmount = toStash.amount + amountDifference.value
		onCommitStashTransfer(
			fromStash.id,
			updatedFromAmount,
			toStash.id,
			updatedToAmount
		)
	}

	override val onDismiss: () -> Unit = {
		_showSheetState.value = false
	}
}

fun clampLong(value: Long, min: Long, max: Long): Long {
	return when {
		value < min -> min
		value > max -> max
		else -> value
	}
}
fun clampDouble(value: Double, min: Double, max: Double): Double {
	return when {
		value < min -> min
		value > max -> max
		else -> value
	}
}