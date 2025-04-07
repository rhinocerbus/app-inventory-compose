@file:OptIn(ExperimentalFoundationApi::class)

package com.piledrive.inventory.ui.screens.units.content

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
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.ui.state.QuantityUnitContentState

object ManageUnitsContentList {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: ManageUnitsContentCoordinatorImpl,
	) {
		val unitsContent = coordinator.unitState.collectAsState().value

		DrawContent(
			modifier,
			unitsContent,
			coordinator,
		)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		unitContent: QuantityUnitContentState,
		coordinator: ManageUnitsContentCoordinatorImpl,
	) {
		val units = unitContent.data.allUnits

		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				when {
					units.isEmpty() -> {
						Text(
							"no units"
						)
						Button(onClick = {
							coordinator.onLaunchCreateUnit()
						}) {
							Text("add unit")
						}
					}

					else -> {
						UnitsList(
							modifier = Modifier.fillMaxSize(),
							units,
							coordinator,
						)

						if (unitContent.isLoading) {
							// secondary spinner?
						}
					}
				}
			}
		}
	}

	@Composable
	internal fun UnitsList(
		modifier: Modifier = Modifier,
		units: List<QuantityUnit>,
		coordinator: ManageUnitsContentCoordinatorImpl,
	) {
		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			LazyColumn {
				itemsIndexed(
					units,
					key = { _, unit -> unit.id }
				) { idx, unit ->
					if (idx > 0) {
						HorizontalDivider(Modifier.fillMaxWidth())
					}
					QuantityUnitListItem(
						Modifier,
						unit,
						coordinator,
					)
				}
			}
		}
	}

	@Composable
	fun QuantityUnitListItem(
		modifier: Modifier = Modifier,
		unit: QuantityUnit,
		coordinator: ManageUnitsContentCoordinatorImpl,
	) {
		Surface(
			modifier = modifier
				.combinedClickable(
					onClick = { coordinator.onUnitClicked(unit) },
					onLongClick = { }
				)
				.fillMaxWidth()
		) {
			Column(
				modifier = modifier
					.padding(8.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text(modifier = Modifier.weight(1f), text = unit.name)
				}
			}
		}
	}
}