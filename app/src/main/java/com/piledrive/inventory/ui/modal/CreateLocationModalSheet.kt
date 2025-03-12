@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.modal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.lib_compose_components.ui.forms.state.TextFormFieldState
import com.piledrive.lib_compose_components.ui.forms.validators.Validators
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import kotlinx.coroutines.flow.StateFlow

interface CreateLocationCallbacks {
	//val onShowCreate: () -> Unit
	val onAddLocation: (slug: LocationSlug) -> Unit
}

val stubCreateLocationCallbacks = object : CreateLocationCallbacks {
	//override val onShowCreate: () -> Unit = {}
	override val onAddLocation: (slug: LocationSlug) -> Unit = {}
}

/*
 todo - integrate show somehow, but with optional params
 - was considering adding to modalsheetcallbacks but that boxes-in or otherwise makes params a pain
 - class-level callback would work i guess
 -- might make modalsheetcallbacks pointless
 more to be seen when making a few versions of this per sheet
 */
class CreateLocationModalSheetCoordinator(
	val showSheetState: MutableState<Boolean> = mutableStateOf(false),
	val createLocationCallbacks: CreateLocationCallbacks = stubCreateLocationCallbacks,
	val modalSheetCallbacks: ModalSheetCallbacks = object : ModalSheetCallbacks {
		override val onDismissed: () -> Unit = {
			showSheetState.value = false
		}
	}
)

object CreateLocationModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateLocationModalSheetCoordinator,
		locationState: StateFlow<LocationContentState>,
	) {
		val sheetState = rememberModalBottomSheetState(
			skipPartiallyExpanded = true
		)
		ModalBottomSheet(
			modifier = Modifier.fillMaxWidth(),
			onDismissRequest = {
				coordinator.modalSheetCallbacks.onDismissed()
			},
			sheetState = sheetState,
			dragHandle = { BottomSheetDefaults.DragHandle() }
		) {
			DrawContent(coordinator = coordinator, locationState)
		}
	}

	@Composable
	internal fun DrawContent(
		coordinator: CreateLocationModalSheetCoordinator,
		locationState: StateFlow<LocationContentState>,
	) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
		) {
			val formState = remember {
				TextFormFieldState(
					mainValidator = Validators.Required(errMsg = "Location name required"),
					externalValidators = listOf(
						Validators.Custom<String>(runCheck = { nameIn ->
							locationState.value.data.allLocations.firstOrNull { it.name.equals(nameIn, true) } == null
						}, "Location with that name exists")
					)
				)
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

				Spacer(Modifier.size(12.dp))

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
						val slug = LocationSlug(name = formState.currentValue)
						coordinator.createLocationCallbacks.onAddLocation(slug)
						coordinator.showSheetState.value = false
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
			coordinator = CreateLocationModalSheetCoordinator(),
			previewLocationContentFlow()
		)
	}
}