@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.screens.main.bars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.enums.SortOrder
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownTextFieldGeneric
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import com.piledrive.lib_compose_components.ui.util.previewBooleanFlow


@Composable
fun MainFilterAppBar(
	modifier: Modifier = Modifier,
	coordinator: MainFilterAppBarCoordinatorImpl
) {
	Surface(color = TopAppBarDefaults.topAppBarColors().containerColor) {
		Column {
			TopAppBar(
				title = {
					Text("What do we have in: ")
				},
				actions = {
				}
			)
			Row(Modifier.fillMaxWidth()) {
				ReadOnlyDropdownTextFieldGeneric<Location>(
					modifier = Modifier.weight(0.5f),
					innerTextFieldModifier = Modifier.wrapContentWidth(),
					coordinator = coordinator.locationsDropdownCoordinator,
					selectionToValueMutator = { "${it.name}" }
				)
				ReadOnlyDropdownTextFieldGeneric<Tag>(
					modifier = Modifier.weight(0.5f),
					innerTextFieldModifier = Modifier.wrapContentWidth(),
					coordinator = coordinator.tagsDropdownCoordinator,
					selectionToValueMutator = { "${it.name}" }
				)
				SortButton(
					modifier = Modifier.size(48.dp),
					coordinator.sortDropdownCoordinator
				)
			}
		}
	}
}

@Composable
internal fun SortButton(modifier: Modifier, coordinator: ReadOnlyDropdownCoordinatorGeneric<SortOrder>) {
	val selectedOption = coordinator.selectedOptionState.value
	val showOptions = coordinator.optionsExpandedState.value
	val optionsPool = coordinator.dropdownOptionsState.value

	IconButton(
		modifier = modifier,
		onClick = {
			coordinator.onOptionsExpandedChanged(!showOptions)
		}
	) {
		Icon(ImageVector.vectorResource(selectedOption?.iconResId ?: -1), contentDescription = "sort by ${selectedOption?.name}")
	}
	DropdownMenu(
		expanded = showOptions,
		onDismissRequest = { coordinator.onOptionsExpandedChanged(false) },
	) {
		optionsPool.forEach { option ->
			DropdownMenuItem(
				onClick = { coordinator.onOptionSelected(option) },
				text = {
					Text("${option.name}")
				}
			)
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
				locationState = previewLocationContentFlow(),
				tagState = previewTagsContentFlow(),
				locationsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
				tagsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
				sortDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
				sortDescendingState = previewBooleanFlow(false)
			)
		)
	}
}
