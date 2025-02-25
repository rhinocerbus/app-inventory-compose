package com.piledrive.inventory.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.piledrive.inventory.model.Location
import com.piledrive.inventory.ui.callbacks.CreateLocationCallbacks
import com.piledrive.inventory.ui.callbacks.ModalSheetCallbacks
import com.piledrive.inventory.ui.callbacks.stubCreateLocationCallbacks
import com.piledrive.inventory.ui.callbacks.stubModalSheetCallbacks
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.util.previewMainContentFlow
import com.piledrive.inventory.viewmodel.SampleViewModel
import kotlinx.coroutines.flow.StateFlow

object MainScreen : NavRoute {
	override val routeValue: String = "home"

	@Composable
	fun draw(
		viewModel: SampleViewModel,
	) {
		var showCreateLocationBottomSheet by remember { mutableStateOf(false) }
		val createLocationCallbacks = object : CreateLocationCallbacks {
			override val onShowCreate: () -> Unit = {
				showCreateLocationBottomSheet = true
			}
		}

		val modalSheetCallbacks = object : ModalSheetCallbacks {
			override val onDismissed: () -> Unit = { showCreateLocationBottomSheet = false }
		}

		drawContent(
			viewModel.locationContentState,
			showCreateLocationBottomSheet,
			createLocationCallbacks,
			modalSheetCallbacks
		)
	}

	@Composable
	fun drawContent(
		contentState: StateFlow<LocationContentState>,
		showCreateLocationBottomSheet: Boolean,
		createLocationCallbacks: CreateLocationCallbacks,
		modalSheetCallbacks: ModalSheetCallbacks,
	) {
		val homeState = contentState.collectAsState().value
		Scaffold(
			topBar = {
			},
			content = { innerPadding ->
				Box(modifier = Modifier.padding(innerPadding))
			}
		)
	}
}

@Preview
@Composable
fun MainPreview() {
	val contentState = previewMainContentFlow()
	MainScreen.drawContent(
		contentState,
		false,
		stubCreateLocationCallbacks,
		stubModalSheetCallbacks
	)
}