@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.screens.main.bars

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.R
import com.piledrive.inventory.data.enums.SortOrder
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.appbar.TopAppBarWithOverflow
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownTextFieldGeneric
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

@Composable
fun MainFilterAppBar(
	modifier: Modifier = Modifier,
	coordinator: MainFilterAppBarCoordinatorImpl,
	onLaunchManageItems: () -> Unit,
	onLaunchManageTags: () -> Unit,
	onLaunchManageUnits: () -> Unit,
	onLaunchManageLocations: () -> Unit,
) {
	val sortDesc = coordinator.sortDescendingState.collectAsState()
	Surface(color = TopAppBarDefaults.topAppBarColors().containerColor) {
		Column {
			TopAppBarWithOverflow.Draw(
				title = {
					Text("What do we have in: ")
				},
				overflowActions = {
					DropdownMenuItem(
						text = { Text("Manage items") },
						onClick = { onLaunchManageItems() }
					)
					DropdownMenuItem(
						text = { Text("Manage tags") },
						onClick = { onLaunchManageTags() }
					)
					DropdownMenuItem(
						text = { Text("Manage units") },
						onClick = { onLaunchManageUnits() }
					)
					DropdownMenuItem(
						text = { Text("Manage locations") },
						onClick = { onLaunchManageLocations() }
					)
				}
			)
			Row(
				Modifier
					.fillMaxWidth()
					.padding(horizontal = 12.dp)
			) {
				ReadOnlyDropdownTextFieldGeneric<Location>(
					modifier = Modifier.weight(0.5f),
					innerTextFieldModifier = Modifier.wrapContentWidth(),
					coordinator = coordinator.locationsDropdownCoordinator
				)
				Gap(8.dp)
				ReadOnlyDropdownTextFieldGeneric<Tag>(
					modifier = Modifier.weight(0.5f),
					innerTextFieldModifier = Modifier.wrapContentWidth(),
					coordinator = coordinator.tagsDropdownCoordinator,
				)
				Gap(8.dp)
				SortButton(
					modifier = Modifier.size(48.dp),
					coordinator = coordinator.sortDropdownCoordinator,
					sortDesc = sortDesc.value,
					onToggle = { coordinator.toggleSortOrder(it) }
				)
			}
		}
	}
}

@Composable
internal fun SortButton(
	modifier: Modifier,
	coordinator: ReadOnlyDropdownCoordinatorGeneric<SortOrder>,
	sortDesc: Boolean,
	onToggle: (Boolean) -> Unit
) {
	Box {
		val selectedOption = coordinator.selectedOptionState.value
		val showOptions = coordinator.optionsExpandedState.value
		val optionsPool = coordinator.dropdownOptionsState.value

		IconButton(
			modifier = modifier,
			onClick = {
				coordinator.onOptionsExpandedChanged(!showOptions)
			}
		) {
			Row {
				Icon(
					ImageVector.vectorResource(selectedOption?.iconResId ?: -1),
					contentDescription = "sort by ${selectedOption?.name}"
				)
				Icon(
					ImageVector.vectorResource(R.drawable.baseline_sort_24),
					contentDescription = "sort by ${selectedOption?.name}",
					modifier = Modifier.graphicsLayer {
						rotationX = if (sortDesc) 0f else 180f
					},
				)
			}
		}
		DropdownMenu(
			expanded = showOptions,
			onDismissRequest = { coordinator.onOptionsExpandedChanged(false) },
		) {
			optionsPool.forEach { option ->
				val isSelected = option == coordinator.selectedOptionState.value
				DropdownMenuItem(
					onClick = {
						if (isSelected) {
							onToggle(!sortDesc)
						} else {
							coordinator.onOptionSelected(option)
						}
					},
					text = {
						Text("${coordinator.optionTextMutator(option)}")
					},
					trailingIcon = {
						if (isSelected) {
							Icon(Icons.Default.Check, "${coordinator.optionTextMutator(option)} selected")
						}
					}
					// can't change background color this way, just use icon for now i suppose
					//colors = MenuDefaults.itemColors().copy()
				)
			}
		}
	}
}

@Preview
@Composable
fun MainFilterAppBarPreview() {
	AppTheme {
		MainFilterAppBar(
			Modifier,
			coordinator = MainFilterAppBarCoordinator(
				locationsSourceFlow = previewLocationContentFlow(),
				tagsSourceFlow = previewTagsContentFlow(),
				locationsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
				tagsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
				sortDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
				sortDesc = false
			),
			onLaunchManageItems = {},
			onLaunchManageTags = {},
			onLaunchManageUnits = {},
			onLaunchManageLocations = {}
		)
	}
}
