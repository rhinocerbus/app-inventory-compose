@file:OptIn(ExperimentalFoundationApi::class)

package com.piledrive.inventory.ui.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.composite.ContentForLocation
import com.piledrive.inventory.data.model.composite.StashForItem
import com.piledrive.inventory.ui.shared.AmountAdjuster
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.coordinators.ListItemOverflowMenuCoordinator
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

interface MainStashContentListCallbacks {
	val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit
	val onStartStashTransfer: (item: Item) -> Unit
}

val stubMainStashContentListCallbacks = object : MainStashContentListCallbacks {
	override val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit = { _, _ -> }
	override val onStartStashTransfer: (item: Item) -> Unit = {}
}

object MainStashContentList {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		currLocationId: String,
		currTagId: String,
		stashes: List<StashForItem>,
		callbacks: MainStashContentListCallbacks
	) {
		val itemDropdownCoordinator = ListItemOverflowMenuCoordinator()
		DrawContent(modifier, currLocationId, currTagId, stashes, callbacks, itemDropdownCoordinator)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		currLocationId: String,
		currTagId: String,
		stashes: List<StashForItem>,
		callbacks: MainStashContentListCallbacks,
		itemDropdownCoordinator: ListItemOverflowMenuCoordinator
	) {
		Surface(
			modifier = modifier,
		) {
			LazyColumn {
				itemsIndexed(
					stashes,
					key = { _, stash ->
						currLocationId + currTagId + stash.stash.id
					}
				) { idx, stash ->
					if (idx > 0) {
						HorizontalDivider(Modifier.fillMaxWidth())
					}
					ItemStashListItem(
						Modifier,
						stash,
						callbacks,
						itemDropdownCoordinator,
						currLocationId == STATIC_ID_LOCATION_ALL,
					)
				}
			}
		}
	}

	@Composable
	fun ItemStashListItem(
		modifier: Modifier = Modifier,
		stashForItem: StashForItem,
		callbacks: MainStashContentListCallbacks,
		coordinator: ListItemOverflowMenuCoordinator,
		readOnly: Boolean
	) {
		val item = stashForItem.item
		val stash = stashForItem.stash
		val unit = stashForItem.quantityUnit
		val tags = stashForItem.tags

		var qtyValue by remember { mutableDoubleStateOf(stash.amount) }

		Surface(
			modifier = modifier
				.combinedClickable(
					onClick = {},
					onLongClick = { coordinator.onChangeMenuForItemId(stash.id) }
				)
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
						readOnly = readOnly,
						onQtyChange = {
							qtyValue = it
							callbacks.onItemStashQuantityUpdated(stash.id, it)
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

			if (coordinator.showMenuForId.value == stash.id) {
				DropdownMenu(
					expanded = true,
					onDismissRequest = { coordinator.onChangeMenuForItemId(null) }
				) {
					DropdownMenuItem(
						text = { Text("Transfer to...") },
						onClick = {
							callbacks.onStartStashTransfer(item)
							coordinator.onChangeMenuForItemId(null)
						}
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun MainStashContentListPreview() {
	AppTheme {
		MainStashContentList.DrawContent(
			modifier = Modifier,
			"",
			"",
			ContentForLocation.generateSampleSet().currentLocationItemStashContent,
			stubMainStashContentListCallbacks,
			ListItemOverflowMenuCoordinator()
		)
	}
}