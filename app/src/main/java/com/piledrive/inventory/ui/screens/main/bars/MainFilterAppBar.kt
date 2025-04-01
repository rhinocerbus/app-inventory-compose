@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.screens.main.bars

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownTextFieldGeneric
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme


@Composable
fun MainFilterAppBar(
	modifier: Modifier = Modifier,
	coordinator: MainFilterAppBarCoordinatorImpl
) {
	TopAppBar(
		title = {
			Text("What's in the: ")
		},
		actions = {
			ReadOnlyDropdownTextFieldGeneric(
				modifier = Modifier.weight(0.4f),
				innerTextFieldModifier = Modifier.wrapContentWidth(),
				coordinator = coordinator.locationsDropdownCoordinator,
				selectionToValueMutator = { "${it.name}" }
			)
			ReadOnlyDropdownTextFieldGeneric(
				modifier = Modifier.weight(0.4f),
				innerTextFieldModifier = Modifier.wrapContentWidth(),
				coordinator = coordinator.tagsDropdownCoordinator,
				selectionToValueMutator = { "${it.name}" }
			)
		}
	)
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
				tagsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric()
			)
		)
	}
}
