@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.items

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
import com.piledrive.inventory.ui.modal.create_item.CreateItemModalSheet
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.stubCreateItemSheetCoordinator
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.screens.items.content.ManageItemsContentCoordinatorImpl
import com.piledrive.inventory.ui.screens.items.content.ManageItemsContentList
import com.piledrive.inventory.ui.screens.items.content.stubManageItemsContentCoordinator
import com.piledrive.inventory.viewmodel.ManageItemsViewModel
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object ManageItemsScreen : NavRoute {
	override val routeValue: String = "items"

	@Composable
	fun draw(
		viewModel: ManageItemsViewModel,
	) {
		drawContent(
			viewModel.contentCoordinator,
			viewModel.contentCoordinator.createItemCoordinator,
		)
	}

	@Composable
	fun drawContent(
		contentCoordinator: ManageItemsContentCoordinatorImpl,
		modalCoordinator: CreateItemSheetCoordinatorImpl,
	) {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text("Manage items") }
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
		listContentCoordinator: ManageItemsContentCoordinatorImpl,
		modalCoordinator: CreateItemSheetCoordinatorImpl,
	) {
		val showItemSheet: Boolean by remember { modalCoordinator.showSheetState }

		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			ManageItemsContentList.Draw(
				modifier = Modifier,
				coordinator = listContentCoordinator,
			)

			if (showItemSheet) {
				CreateItemModalSheet.Draw(Modifier, modalCoordinator)
			}
		}
	}


	@Composable
	fun DrawAddContentFab(
		modifier: Modifier = Modifier,
		modalCoordinator: CreateItemSheetCoordinatorImpl,
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
				Icon(Icons.Default.Add, "Add new item")
			}
		}
	}
}

@Preview
@Composable
fun MainPreview() {
	AppTheme {
		ManageItemsScreen.drawContent(
			stubManageItemsContentCoordinator,
			stubCreateItemSheetCoordinator,
		)
	}
}