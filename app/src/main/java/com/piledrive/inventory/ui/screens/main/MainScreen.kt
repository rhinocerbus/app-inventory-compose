@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.piledrive.inventory.ui.modal.create_item.CreateItemModalSheet
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.stubCreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_item_stash.CreateItemStashModalSheet
import com.piledrive.inventory.ui.modal.create_item_stash.CreateItemStashSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item_stash.stubCreateItemStashSheetCoordinator
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheet
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_location.stubCreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagModalSheet
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_tag.stubCreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitModalSheet
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_unit.stubCreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.modal.transfer_item.TransferItemStashModalSheet
import com.piledrive.inventory.ui.modal.transfer_item.TransferItemStashSheetCoordinatorImpl
import com.piledrive.inventory.ui.modal.transfer_item.stubTransferItemStashSheetCoordinator
import com.piledrive.inventory.ui.nav.NavRoute
import com.piledrive.inventory.ui.screens.main.bars.MainFilterAppBar
import com.piledrive.inventory.ui.screens.main.bars.MainFilterAppBarCoordinatorImpl
import com.piledrive.inventory.ui.screens.main.bars.stubMainFilterAppBarCoordinator
import com.piledrive.inventory.ui.screens.main.content.MainContentListCoordinatorImpl
import com.piledrive.inventory.ui.screens.main.content.MainStashContentList
import com.piledrive.inventory.ui.screens.main.content.stubMainContentListCoordinator
import com.piledrive.inventory.viewmodel.MainViewModel
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object MainScreen : NavRoute {
	override val routeValue: String = "home"

	@Composable
	fun draw(
		viewModel: MainViewModel,
	) {
		drawContent(
			viewModel.listContentCoordinator,
			viewModel.createItemStashCoordinator,
			viewModel.createLocationCoordinator,
			viewModel.createTagCoordinator,
			viewModel.createQuantityUnitSheetCoordinator,
			viewModel.createItemCoordinator,
			viewModel.filterAppBarCoordinator,
			viewModel.transferItemStashSheetCoordinator
		)
	}

	@Composable
	fun drawContent(
		listContentCoordinator: MainContentListCoordinatorImpl,
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinatorImpl,
		createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
		createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
		createItemCoordinator: CreateItemSheetCoordinatorImpl,
		filterBarCoordinator: MainFilterAppBarCoordinatorImpl,
		transferItemStashSheetCoordinator: TransferItemStashSheetCoordinatorImpl
	) {
		Scaffold(
			topBar = {
				MainFilterAppBar(Modifier, filterBarCoordinator)
			},
			content = { innerPadding ->
				DrawBody(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize(),
					listContentCoordinator,
					createItemStashSheetCoordinator,
					createLocationCoordinator,
					createTagCoordinator,
					createQuantityUnitSheetCoordinator,
					createItemCoordinator,
					transferItemStashSheetCoordinator
				)
			},
			floatingActionButton = {
				DrawAddContentFab(
					Modifier,
					createItemStashSheetCoordinator,
					createLocationCoordinator,
					createTagCoordinator,
					createItemCoordinator
				)
			},
		)
	}

	@Composable
	fun DrawBody(
		modifier: Modifier = Modifier,
		listContentCoordinator: MainContentListCoordinatorImpl,
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinatorImpl,
		createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
		createQuantityUnitSheetCoordinator: CreateQuantityUnitSheetCoordinatorImpl,
		createItemCoordinator: CreateItemSheetCoordinatorImpl,
		transferItemStashSheetCoordinator: TransferItemStashSheetCoordinatorImpl
	) {
		val showItemStashSheet: Boolean by remember { createItemStashSheetCoordinator.showSheetState }
		val showLocationSheet: Boolean by remember { createLocationCoordinator.showSheetState }
		val showTagSheet: Boolean by remember { createTagCoordinator.showSheetState }
		val showItemSheet: Boolean by remember { createItemCoordinator.showSheetState }
		val showQuantityUnitSheet: Boolean by remember { createQuantityUnitSheetCoordinator.showSheetState }
		val showTransferSheet: Boolean by remember { transferItemStashSheetCoordinator.showSheetState }

		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {

			MainStashContentList.Draw(
				modifier = Modifier,
				coordinator = listContentCoordinator,
				onLaunchCreateLocation = { createLocationCoordinator.showSheet() },
				onLaunchCreateItemStash = { createItemStashSheetCoordinator.showSheet() }
			)

			if (showItemStashSheet) {
				CreateItemStashModalSheet.Draw(
					Modifier,
					createItemStashSheetCoordinator,
				)
			}

			if (showLocationSheet) {
				CreateLocationModalSheet.Draw(Modifier, createLocationCoordinator)
			}

			if (showItemSheet) {
				CreateItemModalSheet.Draw(
					Modifier,
					createItemCoordinator,
				)
			}

			if (showTagSheet) {
				CreateTagModalSheet.Draw(Modifier, createTagCoordinator)
			}

			if (showQuantityUnitSheet) {
				CreateQuantityUnitModalSheet.Draw(Modifier, createQuantityUnitSheetCoordinator)
			}

			if (showTransferSheet) {
				TransferItemStashModalSheet.Draw(Modifier, transferItemStashSheetCoordinator)
			}
		}
	}


	@Composable
	fun DrawAddContentFab(
		modifier: Modifier = Modifier,
		createItemStashSheetCoordinator: CreateItemStashSheetCoordinatorImpl,
		createLocationCoordinator: CreateLocationModalSheetCoordinatorImpl,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
		createItemCoordinator: CreateItemSheetCoordinatorImpl
	) {
		/*
			box added to satisfy dropdown requirement for a sibling wrapped in a parent to anchor
			https://stackoverflow.com/a/66807367
		 */
		Box {
			var showMenu by remember { mutableStateOf(false) }
			FloatingActionButton(
				onClick = {
					showMenu = true
				}
			) {
				Icon(Icons.Default.Add, "Show 'add content' menu")
			}
			DropdownMenu(
				expanded = showMenu,
				onDismissRequest = { showMenu = false }
			) {
				DropdownMenuItem(
					text = { Text("Add item") }, onClick = {
						createItemStashSheetCoordinator.showSheet()
						showMenu = false
					}
				)
				DropdownMenuItem(
					text = { Text("Add location") }, onClick = {
						createLocationCoordinator.showSheet()
						showMenu = false
					}
				)
				DropdownMenuItem(
					text = { Text("Add tag") }, onClick = {
						createTagCoordinator.showSheet()
						showMenu = false
					}
				)
			}
		}
	}

	@Composable
	fun ColumnScope.DrawLocationItems(modifier: Modifier = Modifier) {
		LazyColumn() { }
	}
}

@Preview
@Composable
fun MainPreview() {
	AppTheme {
		MainScreen.drawContent(
			stubMainContentListCoordinator,
			stubCreateItemStashSheetCoordinator,
			stubCreateLocationModalSheetCoordinator,
			stubCreateTagSheetCoordinator,
			stubCreateQuantityUnitSheetCoordinator,
			stubCreateItemSheetCoordinator,
			stubMainFilterAppBarCoordinator,
			stubTransferItemStashSheetCoordinator,
		)
	}
}