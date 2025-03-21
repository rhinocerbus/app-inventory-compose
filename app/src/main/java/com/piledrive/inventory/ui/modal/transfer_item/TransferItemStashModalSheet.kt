@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal.transfer_item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.ui.shared.AmountAdjuster
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownTextFieldGeneric
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme


object TransferItemStashModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: TransferItemStashSheetCoordinatorImpl,
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
		coordinator: TransferItemStashSheetCoordinatorImpl,
	) {
		val fromStash = coordinator.fromLocationDropdownCoordinator.selectedOptionState.value
		val toStash = coordinator.toLocationDropdownCoordinator.selectedOptionState.value
		val activeItem = coordinator.activeItemState.value ?: throw IllegalStateException("")
		val qtyValue = coordinator.amountDifference.value

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
						selectionToValueMutator = { "${it.location.name} (${it.stash.amount} ${it.quantityUnit.label})" },
					)
				}

				Row(
					modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
				) {
					ReadOnlyDropdownTextFieldGeneric(
						modifier = Modifier.weight(1f),
						coordinator = coordinator.toLocationDropdownCoordinator,
						label = {
							Text(
								text = "To location"
							)
						},
						selectionToValueMutator = { "${it.location.name} (${it.stash.amount} ${it.quantityUnit.label})" },
					)
				}

				Gap(16.dp)

				AmountAdjuster(
					Modifier,
					unit = null,
					qtyValue = qtyValue,
					increment = 1.0,
					max = fromStash?.stash?.amount ?: -1.0,
					readOnly = fromStash == null || toStash == null || qtyValue < 0.0,
					onQtyChange = {
						coordinator.changeTransferAmount(it)
					}
				)
			}
		}
	}
}

@Preview
@Composable
private fun TransferItemStashModalSheetPreview() {
	AppTheme {
		TransferItemStashModalSheet.DrawContent(
			stubTransferItemStashSheetCoordinator,
		)
	}
}
