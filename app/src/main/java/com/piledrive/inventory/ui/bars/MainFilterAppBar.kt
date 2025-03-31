@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.inventory.ui.bars

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


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
			DrawLocationsOptions(coordinator = coordinator)
			DrawTagOptions(coordinator = coordinator)
		}
	)
}

@Composable
private fun DrawLocationsOptions(
	modifier: Modifier = Modifier,
	coordinator: MainFilterAppBarCoordinatorImpl
) {
	val locationsContent = coordinator.locationState.collectAsState().value
	var showLocations by remember { mutableStateOf(false) }
	Button(onClick = { showLocations = true }) {
		Text(locationsContent.data.currentLocation.name)
	}
	DropdownMenu(
		expanded = showLocations,
		onDismissRequest = { showLocations = false }
	) {
		locationsContent.data.allLocations.forEach {
			DropdownMenuItem(
				text = { Text(it.name) }, onClick = {
					coordinator.onLocationChanged(it)
					showLocations = false
				}
			)
		}
	}
}

@Composable
private fun DrawTagOptions(
	modifier: Modifier = Modifier,
	coordinator: MainFilterAppBarCoordinatorImpl
) {
	val tagsContent = coordinator.tagState.collectAsState().value
	var showTags by remember { mutableStateOf(false) }
	Button(onClick = { showTags = true }) {
		Text(tagsContent.data.currentTag.name)
	}
	DropdownMenu(
		expanded = showTags,
		onDismissRequest = { showTags = false }
	) {
		tagsContent.data.allTags.forEach {
			DropdownMenuItem(
				text = { Text(it.name) }, onClick = {
					coordinator.onTagChanged(it)
					showTags = false
				}
			)
		}
	}
}
