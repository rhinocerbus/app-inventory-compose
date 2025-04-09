@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.units

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
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitModalSheet
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_unit.stubCreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.screens.units.content.ManageUnitsContentCoordinatorImpl
import com.piledrive.inventory.ui.screens.units.content.ManageUnitsContentList
import com.piledrive.inventory.ui.screens.units.content.stubManageUnitsContentCoordinator
import com.piledrive.inventory.viewmodel.ManageUnitsViewModel
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object ManageUnitsScreen : NavRoute {
	override val routeValue: String = "units"

	@Composable
	fun draw(
		viewModel: ManageUnitsViewModel,
	) {
		drawContent(
			viewModel.contentCoordinator,
			viewModel.contentCoordinator.createQuantityUnitSheetCoordinator,
		)
	}

	@Composable
	fun drawContent(
		contentCoordinator: ManageUnitsContentCoordinatorImpl,
		createUnitCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
	) {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text("Manage units") }
				)
			},
			content = { innerPadding ->
				DrawBody(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize(),
					contentCoordinator,
					createUnitCoordinator,
				)
			},
			floatingActionButton = {
				DrawAddContentFab(
					Modifier,
					createUnitCoordinator,
				)
			},
		)
	}

	@Composable
	fun DrawBody(
		modifier: Modifier = Modifier,
		listContentCoordinator: ManageUnitsContentCoordinatorImpl,
		createUnitCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
	) {
		val showUnitSheet: Boolean by remember { createUnitCoordinator.showSheetState }

		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			ManageUnitsContentList.Draw(
				modifier = Modifier,
				coordinator = listContentCoordinator,
			)

			if (showUnitSheet) {
				CreateQuantityUnitModalSheet.Draw(Modifier, createUnitCoordinator)
			}
		}
	}


	@Composable
	fun DrawAddContentFab(
		modifier: Modifier = Modifier,
		createUnitCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
	) {
		/*
			box added to satisfy dropdown requirement for a sibling wrapped in a parent to anchor
			https://stackoverflow.com/a/66807367
		 */
		Box {
			FloatingActionButton(
				onClick = {
					createUnitCoordinator.showSheet()
				}
			) {
				Icon(Icons.Default.Add, "Add new unit")
			}
		}
	}
}

@Preview
@Composable
fun MainPreview() {
	AppTheme {
		ManageUnitsScreen.drawContent(
			stubManageUnitsContentCoordinator,
			stubCreateQuantityUnitSheetCoordinator,
		)
	}
}