@file:OptIn(ExperimentalFoundationApi::class)

package com.piledrive.inventory.ui.screens.main.content.multi_location

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.composite.FullItemData
import com.piledrive.inventory.data.model.composite.FullStashData
import com.piledrive.inventory.data.model.composite.StashesForItem
import com.piledrive.inventory.ui.screens.main.content.MainContentListCoordinatorImpl
import com.piledrive.inventory.ui.screens.main.content.stubMainContentListCoordinator
import com.piledrive.inventory.ui.shared.AmountAdjuster
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object MultiLocationStashContent {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: MainContentListCoordinatorImpl,
	) {
		val itemStashContent = coordinator.stashesSourceFlow.collectAsState().value
		val stashes = itemStashContent.data.stashes
		val currLocationId = coordinator.locationsSourceFlow.collectAsState().value.data.currentLocation.id
		val currTagId = coordinator.tagsSourceFlow.collectAsState().value.data.currentTag.id
		val expandedStashes = coordinator.allLocationsSectionsCoordinator.expandedSectionsState.value

		DrawContent(
			modifier,
			coordinator,
			stashes,
			currLocationId,
			currTagId,
			expandedStashes
		)
	}


	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		coordinator: MainContentListCoordinatorImpl,
		stashes: List<StashesForItem>,
		currLocationId: String,
		currTagId: String,
		expandedStashes: List<String>
	) {
		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			LazyColumn {
				itemsIndexed(
					stashes,
					key = { _, stash ->
						/*
						note:
						 using current location & tag state to ensure looking at the same stash while changing location/tags refreshes properly
						 side-product of how composite stashes work for the "all" locations state, could redo how that id is determined to avoid the need
						 */
						currLocationId + currTagId + stash.item.item.id
					}
				) { idx, stash ->
					if (idx > 0) {
						HorizontalDivider(Modifier.fillMaxWidth())
					}
					val expanded = expandedStashes.contains(stash.item.item.id)
					CombinedItemStashListItem(
						Modifier,
						stash,
						expanded,
						coordinator,
					)

					if (expanded) {
						stash.stashes.forEach { s ->
							StashAtLocationListItem(Modifier, stash.item, s, coordinator)
						}
					}
				}
			}
		}
	}

	@Composable
	internal fun CombinedItemStashListItem(
		modifier: Modifier = Modifier,
		stashesForItem: StashesForItem,
		expanded: Boolean,
		coordinator: MainContentListCoordinatorImpl,
	) {
		val fullItem = stashesForItem.item
		val item = fullItem.item
		val unit = fullItem.unit
		val tags = fullItem.tags

		var qtyValue by remember { mutableDoubleStateOf(stashesForItem.totalAmount) }

		Surface(
			modifier = modifier
				.combinedClickable(
					onClick = { coordinator.allLocationsSectionsCoordinator.toggleSectionExpansion(item.id) },
					onLongClick = { coordinator.itemMenuCoordinator.onShowMenuForItemId(item.id) }
				)
				.fillMaxWidth()
		) {
			Column(
				modifier = modifier
					.padding(8.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					if (expanded) {
						Icon(Icons.Default.KeyboardArrowUp, "hide locations for stash")
					} else {
						Icon(Icons.Default.KeyboardArrowDown, "show locations for stash")
					}

					Gap(8.dp)

					Text(modifier = Modifier.weight(1f), text = item.name)

					Gap(8.dp)

					AmountAdjuster(
						Modifier,
						unit = unit,
						qtyValue = qtyValue,
						increment = 1.0,
						readOnly = true,
						hideButtonsIfDisabled = true,
						onQtyChange = {
						}
					)
				}

				Gap(4.dp)
				ChipGroup {
					tags.forEach {
						SuggestionChip(
							onClick = {},
							label = { Text(it.name) },
						)
					}
				}
			}

			if (coordinator.itemMenuCoordinator.showMenuForId.value == item.id) {
				DropdownMenu(
					expanded = true,
					onDismissRequest = { coordinator.itemMenuCoordinator.onDismiss() }
				) {
					DropdownMenuItem(
						text = { Text("Transfer...") },
						onClick = {
							coordinator.startStashTransfer(fullItem, null)
						}
					)
				}
			}
		}
	}

	@Composable
	fun StashAtLocationListItem(modifier: Modifier = Modifier, fullItem: FullItemData, stash: FullStashData, coordinator: MainContentListCoordinatorImpl) {
		Surface(
			modifier = modifier
				.fillMaxWidth()
				.combinedClickable(
					onClick = { coordinator.startStashTransfer(fullItem, stash.location) },
				)
		) {
			Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
				Gap(16.dp)
				Text(
					modifier = Modifier.weight(1f),
					text = stash.location.name
				)
				Gap(8.dp)

				AmountAdjuster(
					Modifier,
					unit = fullItem.unit,
					qtyValue = stash.stash.amount,
					increment = 1.0,
					readOnly = true,
					hideButtonsIfDisabled = true,
					onQtyChange = {}
				)
			}
		}
	}
}

@Preview
@Composable
private fun MultiLocationStashContentPreview() {
	AppTheme {
		val sampleData = StashesForItem.generateSampleSet()

		MultiLocationStashContent.DrawContent(
			modifier = Modifier,
			stubMainContentListCoordinator,
			sampleData,
			"l1",
			"t1",
			listOf()
		)
	}
}