@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.modal.create_location

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.lib_compose_components.ui.forms.state.TextFormFieldState
import com.piledrive.lib_compose_components.ui.forms.validators.Validators
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme


object CreateLocationModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateLocationModalSheetCoordinatorImpl,
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
			DrawContent(coordinator = coordinator)
		}
	}

	@Composable
	internal fun DrawContent(
		coordinator: CreateLocationModalSheetCoordinatorImpl,
	) {
		val activeLocation = coordinator.activeEditDataState.value
		val initialText = remember { activeLocation?.name ?: "" }

		Surface(
			modifier = Modifier
				.fillMaxWidth()
		) {
			val formState = remember {
				TextFormFieldState(
					initialValue = initialText,
					mainValidator = Validators.Required(errMsg = "Location name required"),
					externalValidators = listOf(
						Validators.Custom<String>(runCheck = { nameIn ->
							val matchEdit = nameIn == activeLocation?.name
							val matchExisting =
								coordinator.locationsSourceFlow.value.data.allLocations.firstOrNull { it.name.equals(nameIn, true) } != null
							!matchExisting || matchEdit
						}, "Location with that name exists")
					)
				).apply {
					if(!initialText.isNullOrBlank()) {
						this.check(initialText)
					}
				}
			}

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				OutlinedTextField(
					modifier = Modifier.weight(1f),
					value = formState.currentValue,
					isError = formState.hasError,
					supportingText = {
						if (formState.hasError) {
							Text(
								modifier = Modifier.fillMaxWidth(),
								text = formState.errorMsg ?: "",
								color = MaterialTheme.colorScheme.error
							)
						}
					},
					label = { Text("Location name") },
					onValueChange = { formState.check(it) }
				)

				Gap(12.dp)

				IconButton(
					modifier = Modifier.size(40.dp),
					enabled = formState.isValid,
					onClick = {
						/* todo
								- add another callback layer to have viewmodel do content-level validation (dupe check)
								- dismiss based on success of ^
								- also have error message from ^
								requires fleshing out and/or moving form state to viewmodel, can't decide if better left internal or add
								form-level viewmodel, feels like clutter in the main VM
						 */
						if (activeLocation == null) {
							val slug = LocationSlug(name = formState.currentValue)
							coordinator.onCreateDataModel(slug)
						} else {
							val updatedLocation = activeLocation.copy(name = formState.currentValue)
							coordinator.onUpdateDataModel(updatedLocation)
						}
						coordinator.onDismiss()
					}
				) {
					Icon(Icons.Default.Done, contentDescription = "add new location")
				}
			}
		}
	}
}

@Preview
@Composable
private fun CreateLocationSheetPreview() {
	AppTheme {
		CreateLocationModalSheet.DrawContent(
			coordinator = stubCreateLocationModalSheetCoordinator,
		)
	}
}