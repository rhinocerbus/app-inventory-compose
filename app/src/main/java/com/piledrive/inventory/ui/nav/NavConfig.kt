package com.piledrive.inventory.ui.nav

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.piledrive.inventory.ui.screens.items.ManageItemsScreen
import com.piledrive.inventory.ui.screens.locations.ManageLocationsScreen
import com.piledrive.inventory.ui.screens.main.MainScreen
import com.piledrive.inventory.ui.screens.tags.ManageTagsScreen
import com.piledrive.inventory.ui.screens.units.ManageUnitsScreen
import com.piledrive.inventory.viewmodel.MainViewModel
import com.piledrive.inventory.viewmodel.ManageItemsViewModel
import com.piledrive.inventory.viewmodel.ManageLocationsViewModel
import com.piledrive.inventory.viewmodel.ManageTagsViewModel
import com.piledrive.inventory.viewmodel.ManageUnitsViewModel

interface NavRoute {
	val routeValue: String
}

enum class TopLevelRoutes(override val routeValue: String) : NavRoute {
	SPLASH("splash"), HOME("home")
}

enum class NavArgKeys(val key: String) { GUID("guid") }

enum class ChildRoutes(override val routeValue: String) : NavRoute {
	CONTENT_DETAILS("content/{${NavArgKeys.GUID.key}}"),
}

@Composable
fun RootNavHost() {
	val navController = rememberNavController()
	NavHost(
		modifier = Modifier.safeDrawingPadding(),
		navController = navController,
		startDestination = MainScreen.routeValue
	) {
		composable(route = MainScreen.routeValue) {
			val viewModel: MainViewModel = hiltViewModel<MainViewModel>()
			LaunchedEffect("load_content_on_launch") {
				viewModel.reloadContent()
			}
			MainScreen.draw(
				viewModel,
				onLaunchManageItems = {
					navController.navigate(ManageItemsScreen.routeValue)
				},
				onLaunchManageTags = {
					navController.navigate(ManageTagsScreen.routeValue)
				},
				onLaunchManageUnits = {
					navController.navigate(ManageUnitsScreen.routeValue)
				},
				onLaunchManageLocations = {
					navController.navigate(ManageLocationsScreen.routeValue)
				}
			)
		}

		composable(route = ManageItemsScreen.routeValue) {
			val viewModel: ManageItemsViewModel = hiltViewModel<ManageItemsViewModel>()
			LaunchedEffect("load_items_content_on_launch") {
				viewModel.reloadContent()
			}
			ManageItemsScreen.draw(
				viewModel,
			)
		}

		composable(route = ManageTagsScreen.routeValue) {
			val viewModel: ManageTagsViewModel = hiltViewModel<ManageTagsViewModel>()
			LaunchedEffect("load_tags_content_on_launch") {
				viewModel.reloadContent()
			}
			ManageTagsScreen.draw(
				viewModel,
			)
		}

		composable(route = ManageUnitsScreen.routeValue) {
			val viewModel: ManageUnitsViewModel = hiltViewModel<ManageUnitsViewModel>()
			LaunchedEffect("load_units_content_on_launch") {
				viewModel.reloadContent()
			}
			ManageUnitsScreen.draw(
				viewModel,
			)
		}

		composable(route = ManageLocationsScreen.routeValue) {
			val viewModel: ManageLocationsViewModel = hiltViewModel<ManageLocationsViewModel>()
			LaunchedEffect("load_locations_content_on_launch") {
				viewModel.reloadContent()
			}
			ManageLocationsScreen.draw(
				viewModel,
			)
		}
	}
}

