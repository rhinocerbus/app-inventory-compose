@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal.transfer_item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
		val activeItem = coordinator.activeItemState.value
		val qtyValue = coordinator.amountDifference.value

		Surface(
			modifier = Modifier.fillMaxWidth()
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Transferring ${activeItem?.name ?: "<unset>"}"
				)

				Gap(12.dp)

				ReadOnlyDropdownTextFieldGeneric(
					modifier = Modifier.fillMaxWidth(),
					coordinator = coordinator.fromLocationDropdownCoordinator,
					label = {
						Text(
							text = "From location"
						)
					},
				)

				Gap(12.dp)

				ReadOnlyDropdownTextFieldGeneric(
					modifier = Modifier.fillMaxWidth(),
					coordinator = coordinator.toLocationDropdownCoordinator,
					label = {
						Text(
							text = "To location"
						)
					},
				)

				Gap(16.dp)

				Text(
					text = "Amount to move"
				)

				Gap(12.dp)

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

				Gap(16.dp)

				Button(
					modifier = Modifier,
					onClick = { coordinator.submitTransfer() },
					enabled = fromStash != null && toStash != null && qtyValue > 0.0,
				) {
					Text("Transfer amount")
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
			stubTransferItemStashSheetCoordinator,
		)
	}
}
