@file:OptIn(ExperimentalFoundationApi::class)

package com.piledrive.inventory.ui.screens.main.content.single_location

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
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
import com.piledrive.inventory.data.model.composite.StashesForItem
import com.piledrive.inventory.ui.screens.main.content.MainContentListCoordinatorImpl
import com.piledrive.inventory.ui.screens.main.content.stubMainContentListCoordinator
import com.piledrive.inventory.ui.shared.AmountAdjuster
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object SingleLocationStashContent {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: MainContentListCoordinatorImpl,
	) {
		val itemStashContent = coordinator.stashesSourceFlow.collectAsState().value
		val stashes = itemStashContent.data.stashes
		val currLocationId = coordinator.locationsSourceFlow.collectAsState().value.data.currentLocation.id
		val currTagId = coordinator.tagsSourceFlow.collectAsState().value.data.currentTag.id

		DrawContent(
			modifier,
			coordinator,
			stashes,
			currLocationId,
			currTagId
		)
	}


	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		coordinator: MainContentListCoordinatorImpl,
		stashes: List<StashesForItem>,
		currLocationId: String,
		currTagId: String,
	) {
		if (stashes.firstOrNull { !it.isSingleStash } != null) return
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
						currLocationId + currTagId + stash.stash.id
					}
				) { idx, stash ->
					if (idx > 0) {
						HorizontalDivider(Modifier.fillMaxWidth())
					}
					ItemStashListItem(
						Modifier,
						stash,
						coordinator,
					)
				}
			}
		}
	}

	@Composable
	internal fun ItemStashListItem(
		modifier: Modifier = Modifier,
		stashForItem: StashesForItem,
		coordinator: MainContentListCoordinatorImpl,
	) {
		val item = stashForItem.item.item
		val unit = stashForItem.item.unit
		val tags = stashForItem.item.tags
		val stash = stashForItem.stash

		var qtyValue by remember { mutableDoubleStateOf(stash.amount) }

		Surface(
			modifier = modifier
				.fillMaxWidth()
		) {
			Column(
				modifier = modifier
					.padding(8.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text(modifier = Modifier.weight(1f), text = item.name)
					Gap(8.dp)
					AmountAdjuster(
						Modifier,
						unit = unit,
						qtyValue = qtyValue,
						increment = 1.0,
						readOnly = false,
						onQtyChange = {
							qtyValue = it
							coordinator.onItemStashQuantityUpdated(stash.id, it)
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
		}
	}
}

@Preview
@Composable
private fun SingleLocationStashContentPreview() {
	AppTheme {
		val sampleData = StashesForItem.generateSampleSet()

		SingleLocationStashContent.DrawContent(
			modifier = Modifier,
			stubMainContentListCoordinator,
			sampleData,
			"l1",
			"t1",
		)
	}
}