package com.piledrive.inventory.ui.modal.transfer_item

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.STATIC_ID_NEW_FROM_TRANSFER
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.composite.FullItemData
import com.piledrive.inventory.data.model.composite.FullStashData
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinatorImpl
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import kotlinx.coroutines.flow.StateFlow

interface TransferItemStashSheetCoordinatorImpl : ModalSheetCoordinatorImpl {
	val stashesSourceFlow: StateFlow<ItemStashContentState>
	val itemsSourceFlow: StateFlow<ItemContentState>
	val locationsSourceFlow: StateFlow<LocationContentState>

	val activeItemState: State<FullItemData?>
	val amountDifference: State<Double>
	val modifiedAmount: State<Double>

	val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<FullStashData>
	val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<FullStashData>

	val onCommitStashTransfer: (updatedFromStash: Stash, updatedToStash: Stash) -> Unit

	fun showSheetForItem(forItem: FullItemData)
	fun changeTransferAmount(amount: Double)
	fun submitTransfer()
}

class TransferItemStashSheetCoordinator(
	initialShowSheetValue: Boolean = false,
	initialItemValue: FullItemData? = null,
	initialAmountDifferenceValue: Double = 0.0,
	initialModifiedAmountValue: Double = -1.0,

	override val itemsSourceFlow: StateFlow<ItemContentState>,
	override val locationsSourceFlow: StateFlow<LocationContentState>,
	override val stashesSourceFlow: StateFlow<ItemStashContentState>,

	override val onCommitStashTransfer: (updatedFromStash: Stash, updatedToStash: Stash) -> Unit = { _, _ -> }
) : ModalSheetCoordinator(), TransferItemStashSheetCoordinatorImpl {

	private var toOptionsPool = mutableListOf<FullStashData>()

	@Deprecated(message = "use showSheetForItem", level = DeprecationLevel.ERROR)
	override fun showSheet() {
	}

	private val _activeItemState: MutableState<FullItemData?> = mutableStateOf(initialItemValue)

	override val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<FullStashData> =
		ReadOnlyDropdownCoordinatorGeneric(
			externalOnOptionSelected = { opt ->
				_modifiedAmount.value = 0.0
				_amountDifference.value = 0.0
				val filtered = toOptionsPool.filter { it.stash.id != opt?.stash?.id }
				toLocationDropdownCoordinator.updateOptionsPool(filtered)
				if (opt?.stash?.id == toLocationDropdownCoordinator.selectedOptionState.value?.stash?.id) {
					toLocationDropdownCoordinator.onOptionSelected(null)
				}
			},
			optionTextMutator = {
				val unit = activeItemState.value?.unit ?: throw IllegalStateException("Missing item data")
				"${it.location.name} (${it.stash.amount} ${unit.label})"
			},
			excludeSelected = true,
			optionIdForSelectedCheck = { it.location.id }
		)
	override val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<FullStashData> =
		ReadOnlyDropdownCoordinatorGeneric(
			optionTextMutator = {
				val unit = activeItemState.value?.unit ?: throw IllegalStateException("Missing item data")
				"${it.location.name} (${it.stash.amount} ${unit.label})"
			},
			excludeSelected = true,
			optionIdForSelectedCheck = { it.location.id }
		)


	override val activeItemState: State<FullItemData?> = _activeItemState

	private val _amountDifference: MutableState<Double> = mutableDoubleStateOf(initialAmountDifferenceValue)
	override val amountDifference: State<Double> = _amountDifference
	private val _modifiedAmount: MutableState<Double> = mutableDoubleStateOf(initialModifiedAmountValue)
	override val modifiedAmount: State<Double> = _modifiedAmount

	override fun showSheetForItem(forItem: FullItemData) {
		_activeItemState.value = forItem
		fromLocationDropdownCoordinator.onOptionSelected(null)
		toLocationDropdownCoordinator.onOptionSelected(null)
		_amountDifference.value = 0.0
		reload()

		_showSheetState.value = true
	}

	private fun reload() {
		val itemId = activeItemState.value?.item?.id ?: throw IllegalStateException("Missing item data")

		val items = itemsSourceFlow.value.data.items
		val stashes = stashesSourceFlow.value.data.itemStashes
		val locations = locationsSourceFlow.value.data.userLocations

		val rootItem = items.firstOrNull { it.id == itemId } ?: throw IllegalStateException("no item by given id")
		val stashesForItem = stashes.filter { it.itemId == itemId }

		val fromStashes = mutableListOf<FullStashData>()
		stashesForItem.forEach { stash ->
			if (stash.amount == 0.0) return@forEach
			val atLocation = locations.firstOrNull { it.id == stash.locationId } ?: run { return@forEach }
			fromStashes.add(
				FullStashData(
					stash = stash,
					location = atLocation,
				)
			)
		}
		fromLocationDropdownCoordinator.updateOptionsPool(fromStashes)

		val toStashes = mutableListOf<FullStashData>()
		locations.forEach { location ->
			val stashForLoc = stashesForItem.firstOrNull { it.locationId == location.id }
			toStashes.add(
				FullStashData(
					stash = stashForLoc ?: Stash(STATIC_ID_NEW_FROM_TRANSFER, "", rootItem.id, location.id, 0.0),
					location = location,
				)
			)
		}
		toOptionsPool = toStashes
		toLocationDropdownCoordinator.updateOptionsPool(toOptionsPool.filter { it.stash.id != fromLocationDropdownCoordinator.selectedOptionState.value?.stash?.id })
	}

	override fun changeTransferAmount(amount: Double) {
		val startAmount = fromLocationDropdownCoordinator.selectedOptionState.value?.stash?.amount ?: return
		val clampedAmount = clampDouble(amount, 0.0, startAmount)
		_amountDifference.value = clampedAmount
		val endAmount = startAmount - clampedAmount
		_modifiedAmount.value = endAmount
	}

	override fun submitTransfer() {
		val fromStash = fromLocationDropdownCoordinator.selectedOptionState.value?.stash
			?: throw IllegalStateException("missing data for transfer: from stash")
		val toStash = toLocationDropdownCoordinator.selectedOptionState.value?.stash
			?: throw IllegalStateException("missing data for transfer: from stash")
		val updatedFromStash = fromStash.copy(amount = fromStash.amount - amountDifference.value)
		val updatedToStash = toStash.copy(amount = toStash.amount + amountDifference.value)
		onCommitStashTransfer(
			updatedFromStash,
			updatedToStash
		)
		onDismiss()
	}
}

val stubTransferItemStashSheetCoordinator = TransferItemStashSheetCoordinator(
	itemsSourceFlow = previewItemsContentFlow(),
	locationsSourceFlow = previewLocationContentFlow(),
	stashesSourceFlow = previewItemStashesContentFlow(),
	onCommitStashTransfer = { _, _ -> }
)

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