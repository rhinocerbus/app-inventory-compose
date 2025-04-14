@file:OptIn(ExperimentalFoundationApi::class)

package com.piledrive.inventory.ui.screens.main.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.ui.screens.main.content.multi_location.MultiLocationStashContent
import com.piledrive.inventory.ui.screens.main.content.single_location.SingleLocationStashContent
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object MainStashContentList {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: MainContentListCoordinatorImpl,
		onLaunchCreateLocation: () -> Unit,
		onLaunchCreateItemStash: () -> Unit
	) {
		val itemStashContent = coordinator.stashesSourceFlow.collectAsState().value
		val locationContent = coordinator.locationsSourceFlow.collectAsState().value

		DrawContent(
			modifier,
			locationContent,
			itemStashContent,
			coordinator,
			onLaunchCreateLocation,
			onLaunchCreateItemStash
		)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		locationContent: LocationContentState,
		itemStashContent: LocalizedContentState,
		coordinator: MainContentListCoordinatorImpl,
		onLaunchCreateLocation: () -> Unit,
		onLaunchCreateItemStash: () -> Unit
	) {
		val stashesForLocation = itemStashContent.data.currentLocationItemStashContent
		val currLocationId = locationContent.data.currentLocation.id

		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				when {
					locationContent.data.userLocations.isEmpty() -> {
						if (locationContent.hasLoaded) {
							// empty
							DrawEmptyLocationsState(onLaunchCreateLocation)
						} else {
							// main spinner
							CircularProgressIndicator(
								modifier = Modifier
									.padding(8.dp, 16.dp)
									.zIndex(1f)
							)
						}
					}

					stashesForLocation.isEmpty() -> {
						if (currLocationId == STATIC_ID_LOCATION_ALL) {
							Text(
								"no items anywhere"
							)
							Button(onClick = {
								onLaunchCreateItemStash()
							}) {
								Text("add item")
							}
						} else {
							Text(
								"no items in ${locationContent.data.currentLocation.name}"
							)
							Button(onClick = {
								onLaunchCreateItemStash()
							}) {
								Text("add item")
							}
						}
					}

					else -> {
						if (currLocationId == STATIC_ID_LOCATION_ALL) {
							MultiLocationStashContent.Draw(
								modifier = Modifier.fillMaxSize(),
								coordinator,
							)
						} else {
							SingleLocationStashContent.Draw(
								modifier = Modifier.fillMaxSize(),
								coordinator,
							)
						}

						if (locationContent.isLoading) {
							// secondary spinner?
						}
					}
				}
			}
		}
	}


	@Composable
	fun ColumnScope.DrawEmptyLocationsState(
		onLaunchCreateLocation: () -> Unit
	) {
		// empty
		Text(
			"no locations :("
		)
		Button(onClick = {
			onLaunchCreateLocation()
		}) {
			Text("add location")
		}
	}
}

@Preview
@Composable
private fun MainStashContentListPreview() {
	AppTheme {
		MainStashContentList.DrawContent(
			modifier = Modifier,
			locationContent = LocationContentState(),
			itemStashContent = LocalizedContentState(/*data = ContentForLocation.generateSampleSet()*/),
			stubMainContentListCoordinator,
			onLaunchCreateLocation = {},
			onLaunchCreateItemStash = {}
		)
	}
}