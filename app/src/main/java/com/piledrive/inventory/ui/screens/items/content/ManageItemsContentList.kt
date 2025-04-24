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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.composite.FullItemData
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.util.previewFullItemsContentFlow
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.lists.animatedListItemModifier
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object ManageItemsContentList {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: ManageItemsContentCoordinatorImpl,
	) {
		val itemsContent = coordinator.itemsSourceFlow.collectAsState()
		DrawContent(
			modifier,
			itemsContent,
			coordinator,
		)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		itemsContent: State<FullItemsContentState>,
		coordinator: ManageItemsContentCoordinatorImpl,
	) {
		val items = itemsContent.value.data.fullItems
		val isLoading = itemsContent.value.isLoading

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
							coordinator.launchDataModelCreation()
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

						if (isLoading) {
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
		items: List<FullItemData>,
		coordinator: ManageItemsContentCoordinatorImpl,
	) {
		LazyColumn(
			modifier = modifier.fillMaxSize(),
		) {
			itemsIndexed(
				items,
				key = { _, item -> item.item.id }
			) { idx, loc ->
				if (idx > 0) {
					HorizontalDivider(Modifier.fillMaxWidth())
				}
				ItemListItem(
					animatedListItemModifier(),
					loc,
					coordinator,
				)
			}
		}
	}

	@Composable
	fun ItemListItem(
		modifier: Modifier = Modifier,
		fullItem: FullItemData,
		coordinator: ManageItemsContentCoordinatorImpl,
	) {
		val item = fullItem.item
		val tags = fullItem.tags
		val unit = fullItem.unit

		Surface(
			modifier = modifier
				.combinedClickable(
					onClick = { coordinator.launchDataModelEdit(fullItem) },
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

@Preview
@Composable
private fun ManageItemsContentListPreview() {
	AppTheme {
		ManageItemsContentList.DrawContent(
			Modifier,
			itemsContent = previewFullItemsContentFlow().collectAsState(),
			coordinator = stubManageItemsContentCoordinator
		)
	}
}