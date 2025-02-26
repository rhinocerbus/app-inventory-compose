@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.modal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.ui.callbacks.CreateLocationCallbacks
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.inventory.ui.callbacks.stubCreateLocationCallbacks
import com.piledrive.inventory.ui.forms.state.TextFormFieldState
import com.piledrive.inventory.ui.forms.validators.Validators
import com.piledrive.inventory.ui.theme.AppTheme
import kotlinx.coroutines.launch

object CreateLocationModalSheet {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		modalSheetCallbacks: ModalSheetCallbacks,
		createLocationCallbacks: CreateLocationCallbacks,
	) {
		val sheetState = rememberModalBottomSheetState(
			skipPartiallyExpanded = true
		)
		ModalBottomSheet(
			modifier = Modifier.fillMaxWidth(),
			onDismissRequest = {
				modalSheetCallbacks.onDismissed()
			},
			sheetState = sheetState,
			dragHandle = { BottomSheetDefaults.DragHandle() }
		) {
			DrawContent(createLocationCallbacks = createLocationCallbacks)
		}
	}

	@Composable
	internal fun DrawContent(
		createLocationCallbacks: CreateLocationCallbacks,
	) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
		) {

			val formState = remember {
				TextFormFieldState(
					mainValidator = Validators.Required(errMsg = "Location name required")
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
					label = { Text("Location name") },
					onValueChange = { formState.check(it) }
				)

				Spacer(Modifier.size(12.dp))

				IconButton(
					modifier = Modifier.size(40.dp),
					onClick = { createLocationCallbacks.onAddLocation(formState.currentValue) }) {
					Icon(Icons.Default.Add, contentDescription = "add new location")
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
			createLocationCallbacks = stubCreateLocationCallbacks
		)
	}
}