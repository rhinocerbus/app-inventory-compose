@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.tags

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
import androidx.compose.material3.TopAppBar
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
import com.piledrive.inventory.ui.screens.tags.content.ManageTagsContentCoordinatorImpl
import com.piledrive.inventory.ui.screens.tags.content.ManageTagsContentList
import com.piledrive.inventory.viewmodel.MainViewModel
import com.piledrive.inventory.viewmodel.ManageTagsViewModel
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object ManageTagsScreen : NavRoute {
	override val routeValue: String = "tags"

	@Composable
	fun draw(
		viewModel: ManageTagsViewModel,
	) {
		drawContent(
			viewModel.contentCoordinator,
			viewModel.createTagCoordinator,
		)
	}

	@Composable
	fun drawContent(
		contentCoordinator: ManageTagsContentCoordinatorImpl,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
	) {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text("Manage tags") }
				)
			},
			content = { innerPadding ->
				DrawBody(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize(),
					contentCoordinator,
					createTagCoordinator,
				)
			},
			floatingActionButton = {
				DrawAddContentFab(
					Modifier,
					createTagCoordinator,
				)
			},
		)
	}

	@Composable
	fun DrawBody(
		modifier: Modifier = Modifier,
		listContentCoordinator: ManageTagsContentCoordinatorImpl,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
	) {
		val showTagSheet: Boolean by remember { createTagCoordinator.showSheetState }

		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			ManageTagsContentList.Draw(
				modifier = Modifier,
				coordinator = listContentCoordinator,
			)

			if (showTagSheet) {
				CreateTagModalSheet.Draw(Modifier, createTagCoordinator)
			}
		}
	}


	@Composable
	fun DrawAddContentFab(
		modifier: Modifier = Modifier,
		createTagCoordinator: CreateTagSheetCoordinatorImpl,
	) {
		/*
			box added to satisfy dropdown requirement for a sibling wrapped in a parent to anchor
			https://stackoverflow.com/a/66807367
		 */
		Box {
			FloatingActionButton(
				onClick = {
					createTagCoordinator.showSheet()
				}
			) {
				Icon(Icons.Default.Add, "Add new tag")
			}
		}
	}
}

@Preview
@Composable
fun MainPreview() {
	AppTheme {
		ManageTagsScreen.drawContent(
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