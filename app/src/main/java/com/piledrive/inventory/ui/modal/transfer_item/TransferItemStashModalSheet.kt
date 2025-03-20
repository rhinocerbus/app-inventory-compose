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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.Location
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownTextFieldGeneric
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme


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
