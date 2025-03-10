@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.MutableState
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
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.inventory.ui.forms.state.TextFormFieldState
import com.piledrive.inventory.ui.forms.validators.Validators
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.theme.AppTheme
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import kotlinx.coroutines.flow.StateFlow

interface CreateItemCallbacks {
	//val onShowCreate: () -> Unit
	val onAddItem: (item: ItemSlug) -> Unit
}

val stubCreateItemCallbacks = object : CreateItemCallbacks {
	//override val onShowCreate: () -> Unit = {}
	override val onAddItem: (item: ItemSlug) -> Unit = { }
}

class CreateItemSheetCoordinator(
	val showSheetState: MutableState<Boolean> = mutableStateOf(false),
	val createItemCallbacks: CreateItemCallbacks = stubCreateItemCallbacks,
	val modalSheetCallbacks: ModalSheetCallbacks = object : ModalSheetCallbacks {
		override val onDismissed: () -> Unit = {
			showSheetState.value = false
		}
	}
)

object CreateItemModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateItemSheetCoordinator,
		tagSheetCoordinator: CreateTagSheetCoordinator,
		itemState: StateFlow<ItemContentState>,
		tagsContentState: StateFlow<TagsContentState>
	) {
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
				coordinator.modalSheetCallbacks.onDismissed()
			},
			sheetState = sheetState,
			dragHandle = { BottomSheetDefaults.DragHandle() }
		) {
			DrawContent(
				coordinator,
				tagSheetCoordinator,
				tagsContentState,
				selectedTags,
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
		coordinator: CreateItemSheetCoordinator,
		tagSheetCoordinator: CreateTagSheetCoordinator,
		tagsContentState: StateFlow<TagsContentState>,
		selectedTags: List<String>,
		onTagToggle: (String, Boolean) -> Unit
	) {
		val tags = tagsContentState.collectAsState().value

		Surface(
			modifier = Modifier
				.fillMaxWidth()
		) {
			val formState = remember {
				TextFormFieldState(
					mainValidator = Validators.Required(errMsg = "Item name required")
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
						value = formState.currentValue ?: "",
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

					Spacer(Modifier.size(12.dp))

					IconButton(
						modifier = Modifier.size(40.dp),
						enabled = formState.isValid,
						onClick = {
							// todo - add another callback layer to have viewmodel do content-level validation (dupe check)
							// todo - dismiss based on success of ^
							val item = ItemSlug(
								name = formState.currentValue,
								tags = selectedTags,
								unit = QuantityUnit.defaultUnitBags
							)
							coordinator.createItemCallbacks.onAddItem(item)
						}
					) {
						Icon(Icons.Default.Done, contentDescription = "add new location")
					}
				}

				Spacer(Modifier.size(12.dp))

				Text("Item tags:")
				if (tags.data.userTags.isEmpty()) {
					Text("No added tags yet")
				} else {
					// no proper chip group in compose
					FlowRow(
						horizontalArrangement = Arrangement.spacedBy(7.dp),
						verticalArrangement = Arrangement.spacedBy(7.dp),
					) {

						SuggestionChip(
							onClick = {
								// todo - add single-fire launch param to tag sheet, with callback to flag as selected here
								tagSheetCoordinator.showSheetState.value = true
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
			CreateItemSheetCoordinator(),
			CreateTagSheetCoordinator(),
			previewTagsContentFlow(),
			listOf(),
			onTagToggle = { _, _ -> }
		)
	}
}