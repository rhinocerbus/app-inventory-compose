@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownTextFieldGeneric
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import kotlinx.coroutines.flow.StateFlow


interface TransferItemStashCallbacks {
	val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit
}

val stubTransferItemStashCallbacks = object : TransferItemStashCallbacks {
	override val onCommitStashTransfer: (fromStashId: String, updatedFromAmount: Double, toStashId: String, updatedToAmount: Double) -> Unit =
		{ _, _, _, _ -> }
}

class TransferItemStashSheetCoordinator(
	initialShowSheetValue: Boolean = false,
	initialItemValue: Item? = null,
	initialFromLocationValue: String? = null,
	initialToLocationValue: String? = null,
	initialAmountDifferenceValue: Double = 0.0,

	val stashesState: StateFlow<ItemStashContentState> = previewItemStashesContentFlow(),
	val itemState: StateFlow<ItemContentState> = previewItemsContentFlow(),
	val locationsState: StateFlow<LocationContentState> = previewLocationContentFlow(),

	val fromLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location> = ReadOnlyDropdownCoordinatorGeneric(),
	val toLocationDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location> = ReadOnlyDropdownCoordinatorGeneric(),

	val callbacks: TransferItemStashCallbacks = stubTransferItemStashCallbacks
) : ModalSheetCoordinator(initialShowSheetValue) {
	private val _activeItemState: MutableState<Item?> = mutableStateOf(initialItemValue)
	val activeItemState: State<Item?> = _activeItemState
	private val _locationPoolState: MutableState<List<Location>> = mutableStateOf(listOf())
	val locationPoolState: State<List<Location>> = _locationPoolState
	private val _fromLocationState: MutableState<String?> = mutableStateOf(initialFromLocationValue)
	val fromLocationState: State<String?> = _fromLocationState
	private val _toLocationState: MutableState<String?> = mutableStateOf(initialToLocationValue)
	val toLocationState: State<String?> = _toLocationState
	private val _amountDifference: MutableState<Double> = mutableStateOf(initialAmountDifferenceValue)
	val amountDifference: State<Double> = _amountDifference

	fun showSheetForItem(forItemId: String) {
		val activeItem: Item = itemState.value.data.items.firstOrNull { it.id == forItemId } ?: throw IllegalStateException("")
		_activeItemState.value = activeItem

		val stashesForItem: List<Stash> = stashesState.value.data.itemStashes.filter { it.itemId == forItemId }
		val locIds = stashesForItem.map { it.locationId }
		_locationPoolState.value = locationsState.value.data.userLocations.filter { locIds.contains(it.id) }

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

object TransferItemStashModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: TransferItemStashSheetCoordinator,
	) {
		val sheetState = rememberModalBottomSheetState(
			skipPartiallyExpanded = true
		)
		ModalBottomSheet(
			modifier = Modifier.fillMaxWidth(),
			onDismissRequest = {
				coordinator.onDismiss()
			},
			sheetState = sheetState,
			dragHandle = { BottomSheetDefaults.DragHandle() }
		) {
			DrawContent(
				coordinator,
			)
		}
	}

	@Composable
	internal fun DrawContent(
		coordinator: TransferItemStashSheetCoordinator,
	) {
		val activeItem = coordinator.activeItemState.value ?: throw IllegalStateException("")

		Surface(
			modifier = Modifier.fillMaxWidth()
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(12.dp),
			) {
				Text(
					modifier = Modifier.align(Alignment.CenterHorizontally),
					text = "Transferring ${activeItem.name}"
				)

				Gap(12.dp)

				Row(
					modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
				) {
					/*Text(
						text = "from:"
					)*/

					ReadOnlyDropdownTextFieldGeneric(
						modifier = Modifier.weight(1f),
						coordinator = coordinator.fromLocationDropdownCoordinator,
						label = {
							Text(
								text = "From location"
							)
						},
						selectionToValueMutator = { it.name },
					)
				}

				Row(
					modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
				) {
					ReadOnlyDropdownTextFieldGeneric<Location>(
						modifier = Modifier.weight(1f),
						coordinator = coordinator.toLocationDropdownCoordinator,
						label = {
							Text(
								text = "To location"
							)
						},
						selectionToValueMutator = { it.name },
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun TransferItemStashModalSheetPreview() {
	AppTheme {
		TransferItemStashModalSheet.DrawContent(
			TransferItemStashSheetCoordinator(),
		)
	}
}
