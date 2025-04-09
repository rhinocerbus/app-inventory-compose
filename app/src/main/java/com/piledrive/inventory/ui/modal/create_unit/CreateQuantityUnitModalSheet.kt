@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal.create_unit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.piledrive.inventory.data.model.QuantityType
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.chips.ToggleChip
import com.piledrive.lib_compose_components.ui.forms.state.TextFormFieldState
import com.piledrive.lib_compose_components.ui.forms.validators.Validators
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

/*
	todo - consider:
	 	-- reporting back what was added for ex: nested sheets (add item -> set unit)
 */
object CreateQuantityUnitModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateQuantityUnitSheetCoordinatorImpl,
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
			DrawContent(coordinator)
		}
	}

	@Composable
	internal fun DrawContent(
		coordinator: CreateQuantityUnitSheetCoordinatorImpl,
	) {
		val unitPool = coordinator.unitsSourceFlow.collectAsState().value
		val activeUnit = coordinator.activeEditDataState.value
		val initialText = remember { activeUnit?.name ?: "" }

		val selectedMeasurement = remember {
			val type = activeUnit?.type ?: coordinator.selectedMeasurementState.value
			mutableStateOf(type)
		}


		Surface(
			modifier = Modifier
				.fillMaxWidth()
		) {
			// todo - hoist out of here, finish making whole-form state, probably add to coordinator
			val nameFieldState = remember {
				TextFormFieldState(
					initialValue = initialText,
					mainValidator = Validators.Required(errMsg = "Unit name required"),
					externalValidators = listOf(
						Validators.Custom<String>(runCheck = { nameIn ->
							val matchEdit = nameIn == activeUnit?.name
							val matchExisting = unitPool.data.allUnits.firstOrNull { it.name.equals(nameIn, true) } != null
							!matchExisting || matchEdit
						}, errMsg = "Unit already exists")
					)
				).apply {
					if (!initialText.isNullOrBlank()) {
						this.check(initialText)
					}
				}
			}
			val labelFieldState = remember {
				TextFormFieldState(
					initialValue = initialText,
					mainValidator = Validators.Required(errMsg = "Unit label required"),
					externalValidators = listOf(
						Validators.Custom<String>(runCheck = { nameIn ->
							val matchEdit = nameIn == activeUnit?.label
							val matchExisting = unitPool.data.allUnits.firstOrNull { it.label.equals(nameIn, true) } != null
							!matchExisting || matchEdit
						}, errMsg = "Label already exists")
					)
				)
			}

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 12.dp),
				//horizontalAlignment = Alignment.CenterHorizontally
			) {

				ConstraintLayout(
					modifier = Modifier
						.fillMaxWidth()
						.padding(0.dp)
				) {
					val (nameInput, labelInput, createBtn) = createRefs()

					OutlinedTextField(
						modifier = Modifier
							.constrainAs(nameInput, constrainBlock = {
								top.linkTo(parent.top)
								bottom.linkTo(labelInput.top)
								start.linkTo(parent.start)
								end.linkTo(createBtn.start)
								width = Dimension.fillToConstraints
							}),
						value = nameFieldState.currentValue ?: "",
						isError = nameFieldState.hasError,
						supportingText = {
							if (nameFieldState.hasError) {
								Text(
									modifier = Modifier.fillMaxWidth(),
									text = nameFieldState.errorMsg ?: "",
									color = MaterialTheme.colorScheme.error
								)
							}
						},
						label = { Text("Unit name") },
						onValueChange = { nameFieldState.check(it) }
					)

					OutlinedTextField(
						modifier = Modifier
							.constrainAs(labelInput, constrainBlock = {
								top.linkTo(nameInput.bottom)
								bottom.linkTo(parent.bottom)
								start.linkTo(nameInput.start)
								end.linkTo(nameInput.end)
								width = Dimension.fillToConstraints
							}),
						value = labelFieldState.currentValue ?: "",
						isError = labelFieldState.hasError,
						supportingText = {
							if (labelFieldState.hasError) {
								Text(
									modifier = Modifier.fillMaxWidth(),
									text = labelFieldState.errorMsg ?: "",
									color = MaterialTheme.colorScheme.error
								)
							}
						},
						label = { Text("Unit label") },
						onValueChange = { labelFieldState.check(it) }
					)

					//Gap(12.dp)

					IconButton(
						modifier = Modifier
							.size(40.dp)
							.constrainAs(createBtn, constrainBlock = {
								top.linkTo(nameInput.top)
								bottom.linkTo(nameInput.bottom)
								start.linkTo(nameInput.end)
								end.linkTo(parent.end)
							}),
						enabled = nameFieldState.isValid && labelFieldState.isValid,
						onClick = {
							/* todo
									- add another callback layer to have viewmodel do content-level validation (dupe check)
									- dismiss based on success of ^
									- also have error message from ^
									requires fleshing out and/or moving form state to viewmodel, can't decide if better left internal or add
									form-level viewmodel, feels like clutter in the main VM
							 */
							if (activeUnit == null) {
								val slug = QuantityUnitSlug(
									name = nameFieldState.currentValue,
									label = labelFieldState.currentValue,
									type = coordinator.selectedMeasurementState.value
								)
								coordinator.onCreateDataModel(slug)
							} else {
								val updatedUnit = activeUnit.copy(
									name = nameFieldState.currentValue,
									label = labelFieldState.currentValue,
									type = selectedMeasurement.value
								)
								coordinator.onUpdateDataModel(updatedUnit)
							}
							coordinator.onDismiss()
						}
					) {
						Icon(Icons.Default.Done, contentDescription = "add new location")
					}

					createHorizontalChain(
						nameInput.withChainParams(),
						createBtn.withChainParams(startMargin = 12.dp),
						chainStyle = ChainStyle.Spread
					)
				}

				Gap(8.dp)
				HorizontalDivider(
					Modifier
						.fillMaxWidth(0.9f)
						.align(Alignment.CenterHorizontally)
				)
				Gap(8.dp)

				Text("Measurement:")
				ChipGroup {
					QuantityType.values().forEach {
						if (it == QuantityType.UNKNOWN) {
							return@forEach
						}
						ToggleChip(
							onClick = { selectedMeasurement.value = it },
							label = { Text(text = "${it.name}") },
							selected = it == selectedMeasurement.value
						)
					}
				}

				Gap(8.dp)
				HorizontalDivider(
					Modifier
						.fillMaxWidth(0.9f)
						.align(Alignment.CenterHorizontally)
				)
				Gap(8.dp)

				Text("Current units:")
				if (unitPool.data.allUnits.isEmpty()) {
					Text("No added units yet")
				} else {
					ChipGroup {
						unitPool.data.allUnits.forEach {
							SuggestionChip(
								onClick = {},
								label = { Text("${it.name} (${it.label})") },
							)
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun CreateQuantityUnitSheetPreview() {
	AppTheme {
		CreateQuantityUnitModalSheet.DrawContent(
			stubCreateQuantityUnitSheetCoordinator,
		)
	}
}