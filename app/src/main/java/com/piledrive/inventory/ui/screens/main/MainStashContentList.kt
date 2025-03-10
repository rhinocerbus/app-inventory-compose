@file:OptIn(ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.main

import android.text.TextPaint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.composite.ContentForLocation
import com.piledrive.inventory.data.model.composite.StashForItem
import com.piledrive.inventory.ui.theme.AppTheme
import com.piledrive.inventory.ui.util.MeasureTextWidth

interface MainStashContentListCallbacks {
	val onItemStashQuantityUpdated: () -> Unit
}

val stubMainStashContentListCallbacks = object : MainStashContentListCallbacks {
	override val onItemStashQuantityUpdated: () -> Unit = { }
}

object MainStashContentList {
	@Composable
	fun Draw(modifier: Modifier = Modifier, stashes: List<StashForItem>, callbacks: MainStashContentListCallbacks) {
		DrawContent(modifier, stashes, callbacks)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		stashes: List<StashForItem>,
		callbacks: MainStashContentListCallbacks
	) {
		Surface(
			modifier = modifier,
		) {
			LazyColumn(
			) {
				itemsIndexed(
					stashes,
					key = { _, stash ->
						stash.item.id
					}
				) { _, stash ->
					ItemStashListItem(Modifier, stash)
				}
			}
		}
	}

	@Composable
	fun ItemStashListItem(modifier: Modifier = Modifier, stashForItem: StashForItem) {
		val item = stashForItem.item
		val stash = stashForItem.stash
		val tags = stashForItem.tags
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
					IconButton(
						onClick = {},
						enabled = stash.amount > 0
					) {
						Icon(Icons.Default.KeyboardArrowDown, "decrement item stash amount")
					}

					val amountW =
						MeasureTextWidth("00.00", MaterialTheme.typography.bodySmall, TextPaint())

					OutlinedTextField(
						modifier = Modifier.width(amountW.dp),
						value = "${stash.amount}",
						onValueChange = {

						},
						textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
						singleLine = true
					)
					Spacer(Modifier.size(8.dp))
					Text("${item.unit.label}")

					IconButton(
						onClick = {},
						enabled = true
					) {
						Icon(Icons.Default.KeyboardArrowUp, "increment item stash amount")
					}
				}

				Spacer(Modifier.size(4.dp))
				FlowRow(
					horizontalArrangement = Arrangement.spacedBy(6.dp),
					verticalArrangement = Arrangement.spacedBy(6.dp),
				) {
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
private fun MainStashContentListPreview() {
	AppTheme {
		MainStashContentList.DrawContent(
			modifier = Modifier,
			ContentForLocation.generateSampleSet().locationsScopedContent.values.first(),
			stubMainStashContentListCallbacks
		)
	}
}