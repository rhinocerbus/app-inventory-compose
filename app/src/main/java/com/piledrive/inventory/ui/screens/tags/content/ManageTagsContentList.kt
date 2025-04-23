@file:OptIn(ExperimentalFoundationApi::class)

package com.piledrive.inventory.ui.screens.tags.content

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
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.lib_compose_components.ui.lists.animatedListItemModifier

object ManageTagsContentList {
	@Composable
	fun Draw(
		modifier: Modifier = Modifier,
		coordinator: ManageTagsContentCoordinatorImpl,
	) {
		val tagsContent = coordinator.tagsSourceFlow.collectAsState().value

		DrawContent(
			modifier,
			tagsContent,
			coordinator,
		)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		tagContent: TagsContentState,
		coordinator: ManageTagsContentCoordinatorImpl,
	) {
		val tags = tagContent.data.userTags

		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				when {
					tags.isEmpty() -> {
						Text(
							"no tags"
						)
						Button(onClick = {
							coordinator.launchDataModelCreation()
						}) {
							Text("add tag")
						}
					}

					else -> {
						TagsList(
							modifier = Modifier.fillMaxSize(),
							tags,
							coordinator,
						)

						if (tagContent.isLoading) {
							// secondary spinner?
						}
					}
				}
			}
		}
	}

	@Composable
	internal fun TagsList(
		modifier: Modifier = Modifier,
		tags: List<Tag>,
		coordinator: ManageTagsContentCoordinatorImpl,
	) {
		Surface(
			modifier = modifier.fillMaxSize(),
		) {
			LazyColumn {
				itemsIndexed(
					tags,
					key = { _, tag -> tag.id }
				) { idx, tag ->
					if (idx > 0) {
						HorizontalDivider(Modifier.fillMaxWidth())
					}
					TagListItem(
						animatedListItemModifier(),
						tag,
						coordinator,
					)
				}
			}
		}
	}

	@Composable
	fun TagListItem(
		modifier: Modifier = Modifier,
		tag: Tag,
		coordinator: ManageTagsContentCoordinatorImpl,
	) {
		Surface(
			modifier = modifier
				.combinedClickable(
					onClick = { coordinator.launchDataModelEdit(tag) },
					onLongClick = { }
				)
				.fillMaxWidth()
		) {
			Column(
				modifier = modifier
					.padding(8.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text(modifier = Modifier.weight(1f), text = tag.name)
				}
			}
		}
	}
}