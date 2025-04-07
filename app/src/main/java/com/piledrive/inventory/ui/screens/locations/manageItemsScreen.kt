@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.locations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheet
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_location.stubCreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.screens.locations.content.ManageLocationsContentCoordinatorImpl
import com.piledrive.inventory.ui.screens.locations.content.ManageLocationsContentList
import com.piledrive.inventory.ui.screens.locations.content.stubManageLocationsContentCoordinator
import com.piledrive.inventory.viewmodel.ManageLocationsViewModel
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object ManageLocationsScreen : NavRoute {
	override val routeValue: String = "locations"

	@Composable
	fun draw(
		viewModel: ManageLocationsViewModel,
	) {
		drawContent(
			viewModel.contentCoordinator,
			viewModel.createLocationCoordinator,
		)
	}

	@Composable
	fun drawContent(
		contentCoordinator: ManageLocationsContentCoordinatorImpl,
		modalCoordinator: CreateLocationModalSheetCoordinatorImpl,
	) {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text("Manage locations") }
				)
			},
			content = { innerPadding ->
				DrawBody(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize(),
					contentCoordinator,
					modalCoordinator,
				)
			},
			floatingActionButton = {
				DrawAddContentFab(
					Modifier,
					modalCoordinator,
				)
			},
		)
	}

	@Composable
	fun DrawBody(
		modifier: Modifier = Modifier,
		listContentCoordinator: ManageLocationsContentCoordinatorImpl,
		modalCoordinator: CreateLocationModalSheetCoordinatorImpl,
	) {
		val showLocationSheet: Boolean by remember { modalCoordinator.showSheetState }

		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			ManageLocationsContentList.Draw(
				modifier = Modifier,
				coordinator = listContentCoordinator,
			)

			if (showLocationSheet) {
				CreateLocationModalSheet.Draw(Modifier, modalCoordinator)
			}
		}
	}


	@Composable
	fun DrawAddContentFab(
		modifier: Modifier = Modifier,
		modalCoordinator: CreateLocationModalSheetCoordinatorImpl,
	) {
		/*
			box added to satisfy dropdown requirement for a sibling wrapped in a parent to anchor
			https://stackoverflow.com/a/66807367
		 */
		Box {
			FloatingActionButton(
				onClick = {
					modalCoordinator.showSheet()
				}
			) {
				Icon(Icons.Default.Add, "Add new location")
			}
		}
	}
}

@Preview
@Composable
fun MainPreview() {
	AppTheme {
		ManageLocationsScreen.drawContent(
			stubManageLocationsContentCoordinator,
			stubCreateLocationModalSheetCoordinator,
		)
	}
}