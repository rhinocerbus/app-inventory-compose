@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.modal.create_tag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.forms.state.TextFormFieldState
import com.piledrive.lib_compose_components.ui.forms.validators.Validators
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme


/*
	todo - consider:
	  - single/multi-use
	 	-- reporting back what was added for ex: nested sheets (add item -> add tag)
 */
object CreateTagModalSheet {

	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: CreateTagSheetCoordinatorImpl,
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
		coordinator: CreateTagSheetCoordinatorImpl,
	) {
		val tags = coordinator.tagsContentState.collectAsState().value
		val activeTag = coordinator.activeEditDataState.value
		val initialText = remember { activeTag?.name ?: "" }

		Surface(
			modifier = Modifier
				.fillMaxWidth()
		) {
			val formState = remember {
				TextFormFieldState(
					initialValue = initialText,
					mainValidator = Validators.Required(errMsg = "Tag name required"),
					externalValidators = listOf(
						Validators.Custom<String>(runCheck = { nameIn ->
							val matchEdit = nameIn == activeTag?.name
							val matchExisting =
								tags.data.allTags.firstOrNull { it.name.equals(nameIn, true) } != null
							!matchExisting || matchEdit
						}, errMsg = "Tag already exists")
					)
				).apply {
					if(!initialText.isNullOrBlank()) {
						this.check(initialText)
					}
				}
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
						label = { Text("Tag name") },
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
							if(activeTag == null) {
								val slug = TagSlug(name = formState.currentValue)
								coordinator.onAddTag(slug)
							} else {
								val updatedTag = activeTag.copy(name = formState.currentValue)
								coordinator.onUpdateTag(updatedTag)
							}
							coordinator.onDismiss()
						}
					) {
						Icon(Icons.Default.Done, contentDescription = "add new tag")
					}
				}

				Gap(12.dp)

				Text("Current tags:")
				if (tags.data.userTags.isEmpty()) {
					Text("No added tags yet")
				} else {
					ChipGroup {
						tags.data.userTags.forEach {
							SuggestionChip(
								onClick = {},
								label = { Text(it.name) },
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
private fun CreateTagSheetPreview() {
	AppTheme {
		CreateTagModalSheet.DrawContent(
			stubCreateTagSheetCoordinator,
		)
	}
}