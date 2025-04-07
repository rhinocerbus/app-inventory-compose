@file:OptIn(ExperimentalFoundationApi::class)

package com.piledrive.inventory.ui.screens.items.content

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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.spacer.Gap

object ManageItemsContentList {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: ManageItemsContentCoordinatorImpl,
	) {
		val itemsContent = coordinator.itemState.collectAsState().value

		DrawContent(
			modifier,
			itemsContent,
			coordinator,
		)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		itemsContent: FullItemsContentState,
		coordinator: ManageItemsContentCoordinatorImpl,
	) {
		val items = itemsContent.data.fullItems

		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				when {
					items.isEmpty() -> {
						Text(
							"no items"
						)
						Button(onClick = {
							coordinator.onLaunchCreateItem()
						}) {
							Text("add item")
						}
					}

					else -> {
						ItemsList(
							modifier = Modifier.fillMaxSize(),
							items,
							coordinator,
						)

						if (itemsContent.isLoading) {
							// secondary spinner?
						}
					}
				}
			}
		}
	}

	@Composable
	internal fun ItemsList(
		modifier: Modifier = Modifier,
		items: List<ItemWithTags>,
		coordinator: ManageItemsContentCoordinatorImpl,
	) {
		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			LazyColumn {
				itemsIndexed(
					items,
					key = { _, item -> item.item.id }
				) { idx, loc ->
					if (idx > 0) {
						HorizontalDivider(Modifier.fillMaxWidth())
					}
					ItemListItem(
						Modifier,
						loc,
						coordinator,
					)
				}
			}
		}
	}

	@Composable
	fun ItemListItem(
		modifier: Modifier = Modifier,
		fullItem: ItemWithTags,
		coordinator: ManageItemsContentCoordinatorImpl,
	) {
		val item = fullItem.item
		val tags = fullItem.tags
		val unit = fullItem.quantityUnit

		Surface(
			modifier = modifier
				.combinedClickable(
					onClick = { coordinator.onItemClicked(fullItem) },
					onLongClick = { }
				)
				.fillMaxWidth()
		) {
			Column(
				modifier = modifier
					.padding(8.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text(modifier = Modifier.weight(1f), text = item.name)
					Text(modifier = Modifier, text = unit.label)
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