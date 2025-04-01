@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal.create_item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.forms.state.TextFormFieldState
import com.piledrive.lib_compose_components.ui.forms.validators.Validators
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object CreateItemModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateItemSheetCoordinatorImpl,
	) {
		var selectedQuantityUnit: String? by remember { mutableStateOf(null) }
		var selectedTags by remember { mutableStateOf(listOf<String>()) }
		/*
			or
			val selectedTags = remember { mutableStateListOf<String>() }
		 */

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
				selectedQuantityUnit,
				selectedTags,
				onQuantityUnitChange = {
					selectedQuantityUnit = it
				},
				onTagToggle = { id, update ->
					selectedTags = if (update) {
						selectedTags + id
					} else {
						selectedTags - id
					}
				}
			)
		}
	}

	@Composable
	internal fun DrawContent(
		coordinator: CreateItemSheetCoordinatorImpl,
		selectedQuantityUnit: String?,
		selectedTags: List<String>,
		onQuantityUnitChange: (String) -> Unit,
		onTagToggle: (String, Boolean) -> Unit
	) {
		val quantityUnits = coordinator.quantityContentState.collectAsState().value
		val tags = coordinator.tagsContentState.collectAsState().value

		Surface(
			modifier = Modifier
				.fillMaxWidth()
		) {
			val formState = remember {
				TextFormFieldState(
					mainValidator = Validators.Required(errMsg = "Item name required"),
					externalValidators = listOf(
						Validators.Custom(runCheck = { nameIn ->
							coordinator.itemState.value.data.items.firstOrNull { it.name.equals(nameIn, true) } == null
						}, "Item with that name already exists")
					)
				)
			}

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 12.dp),
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
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
						label = { Text("Item name") },
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
							val item = ItemSlug(
								name = formState.currentValue,
								unitId = selectedQuantityUnit ?: QuantityUnit.DEFAULT_ID_BAGS,
								tagIds = selectedTags,
							)
							coordinator.onAddItem(item)
							coordinator.onDismiss()
						}
					) {
						Icon(Icons.Default.Done, contentDescription = "add new location")
					}
				}

				Gap(12.dp)

				Text("Quantity unit:")
				ChipGroup {
					SuggestionChip(
						onClick = {
							coordinator.onLaunchAddUnit()
						},
						label = { Text("Add") },
						icon = { Icon(Icons.Default.Add, "add new quantity unit") }
					)

					quantityUnits.data.allUnits.forEach {
						val selected = selectedQuantityUnit == it.id
						FilterChip(
							selected = selected,
							onClick = {
								onQuantityUnitChange(it.id)
							},
							label = { Text("${it.name} (${it.label})") },
							leadingIcon = {
								if (selected) {
									Icon(
										Icons.Default.Check,
										"${it.name} applied",
										Modifier.size(FilterChipDefaults.IconSize),
									)
								} else {
									null
								}
							}
						)
					}
				}

				Gap(12.dp)

				Text("Item tags:")
				if (tags.data.userTags.isEmpty()) {
					Text("No added tags yet")
				} else {
					ChipGroup {
						SuggestionChip(
							onClick = {
								// todo - add single-fire launch param to tag sheet, with callback to flag as selected here
								coordinator.onLaunchAddTag()
							},
							label = { Text("Add") },
							icon = { Icon(Icons.Default.Add, "add new tag") }
						)

						tags.data.userTags.forEach {
							val selected = selectedTags.contains(it.id)
							FilterChip(
								selected = selected,
								onClick = {
									onTagToggle(it.id, !selected)
								},
								label = { Text(it.name) },
								leadingIcon = {
									if (selected) {
										Icon(
											Icons.Default.Check,
											"${it.name} applied",
											Modifier.size(FilterChipDefaults.IconSize),
										)
									} else {
										null
									}
								}
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
private fun CreateItemSheetPreview() {
	AppTheme {
		CreateItemModalSheet.DrawContent(
			stubCreateItemSheetCoordinator,
			null,
			listOf(),
			onQuantityUnitChange = {},
			onTagToggle = { _, _ -> }
		)
	}
}