@file:OptIn(ExperimentalFoundationApi::class)

package com.piledrive.inventory.ui.screens.locations.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.lib_compose_components.ui.lists.animatedListItemModifier

object ManageLocationsContentList {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: ManageLocationsContentCoordinatorImpl,
	) {
		val locationsContent = coordinator.locationsSourceFlow.collectAsState().value

		DrawContent(
			modifier,
			locationsContent,
			coordinator,
		)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		locationsContent: LocationContentState,
		coordinator: ManageLocationsContentCoordinatorImpl,
	) {
		val locations = locationsContent.data.userLocations

		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				when {
					locations.isEmpty() -> {
						Text(
							"no locations"
						)
						Button(onClick = {
							coordinator.launchDataModelCreation()
						}) {
							Text("add location")
						}
					}

					else -> {
						LocationsList(
							modifier = Modifier.fillMaxSize(),
							locations,
							coordinator,
						)

						if (locationsContent.isLoading) {
							// secondary spinner?
						}
					}
				}
			}
		}
	}

	@Composable
	internal fun LocationsList(
		modifier: Modifier = Modifier,
		locations: List<Location>,
		coordinator: ManageLocationsContentCoordinatorImpl,
	) {
		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			LazyColumn {
				itemsIndexed(
					locations,
					key = { _, tag -> tag.id }
				) { idx, loc ->
					if (idx > 0) {
						HorizontalDivider(Modifier.fillMaxWidth())
					}
					LocationListItem(
						animatedListItemModifier(),
						loc,
						coordinator,
					)
				}
			}
		}
	}

	@Composable
	fun LocationListItem(
		modifier: Modifier = Modifier,
		location: Location,
		coordinator: ManageLocationsContentCoordinatorImpl,
	) {
		Surface(
			modifier = modifier
				.combinedClickable(
					onClick = { coordinator.launchDataModelEdit(location) },
					onLongClick = { }
				)
				.fillMaxWidth()
		) {
			Column(
				modifier = modifier
					.padding(8.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text(modifier = Modifier.weight(1f), text = location.name)
				}
			}
		}
	}
}