@file:OptIn(ExperimentalLayoutApi::class)

package com.piledrive.inventory.ui.screens.main

import android.text.TextPaint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.composite.ContentForLocation
import com.piledrive.inventory.data.model.composite.StashForItem
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.util.MeasureTextWidth

interface MainStashContentListCallbacks {
	val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit
}

val stubMainStashContentListCallbacks = object : MainStashContentListCallbacks {
	override val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit = { _, _ -> }
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
		DrawContent(modifier, currLocationId, currTagId, stashes, callbacks)
	}

	@Composable
	internal fun DrawContent(
		modifier: Modifier = Modifier,
		currLocationId: String,
		currTagId: String,
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
						currLocationId + currTagId + stash.stash.id
					}
				) { idx, stash ->
					if(idx > 0) {
						HorizontalDivider(Modifier.fillMaxWidth())
					}
					ItemStashListItem(
						Modifier,
						stash,
						callbacks,
						currLocationId == STATIC_ID_LOCATION_ALL
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
		readOnly: Boolean
	) {
		val item = stashForItem.item
		val stash = stashForItem.stash
		val unit = stashForItem.quantityUnit
		val tags = stashForItem.tags

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
					Text("(${unit.label})")
					Gap(8.dp)

					IconButton(
						onClick = {
							qtyValue -= 1.0
							callbacks.onItemStashQuantityUpdated(stash.id, qtyValue)
						},
						enabled = qtyValue > 0 && !readOnly
					) {
						Icon(Icons.Default.KeyboardArrowDown, "decrement item stash amount")
					}

					val amountW =
						MeasureTextWidth("00.00", MaterialTheme.typography.bodySmall, TextPaint())

					OutlinedTextField(
						modifier = Modifier.width(amountW.dp).focusable(!readOnly),
						value = "${qtyValue}",
						onValueChange = {
							if (it.toDouble() < 0) {
								//err
							} else {
								qtyValue = it.toDouble()
								callbacks.onItemStashQuantityUpdated(stash.id, qtyValue)
							}
						},
						textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
						singleLine = true,
						readOnly = readOnly
					)

					IconButton(
						onClick = {
							qtyValue += 1.0
							callbacks.onItemStashQuantityUpdated(stash.id, qtyValue)
						},
						enabled = !readOnly
					) {
						Icon(Icons.Default.KeyboardArrowUp, "increment item stash amount")
					}
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
private fun MainStashContentListPreview() {
	AppTheme {
		MainStashContentList.DrawContent(
			modifier = Modifier,
			"",
			"",
			ContentForLocation.generateSampleSet().currentLocationItemStashContent,
			stubMainStashContentListCallbacks
		)
	}
}