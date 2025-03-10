package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.state.LocalizedContentState

data class ContentForLocation(
	val locationsScopedContent: Map<String, List<StashForItem>> = mapOf()
) {

	companion object {
		fun generateSampleSet(): ContentForLocation {
			val locationId = "asdf"
			val stashes = listOf<StashForItem>(
				StashForItem(
					Stash(
						id = "s1",
						createdAt ="",
						itemId = "i1",
						amount = 99.99,
						locationId = locationId
					),
					item = Item(
						id = "i1",
						createdAt = "",
						name = "item 1",
						tags = listOf("t1", "t2"),
						unit = QuantityUnit.defaultUnitBags
					),
					tags = listOf(
						Tag(id = "t1", createdAt = "", name = "meat"),
						Tag(id = "t2", createdAt = "", name = "leftovers"),
					)
				)
			)
			val content = ContentForLocation(mapOf(Pair(locationId, stashes)))
			return content
		}
	}

	val flatContent: List<StashForItem>
		get() {
			val consolidatedMap = mutableMapOf<String, StashForItem>()
			locationsScopedContent.values.forEach { items ->
				items.forEach { item ->
					val oldStash = consolidatedMap[item.item.id]
					if(oldStash != null) {
						consolidatedMap[item.item.id] = oldStash.copy(stash = oldStash.stash.copy(amount = oldStash.stash.amount + item.stash.amount))
					} else {
						consolidatedMap[item.item.id] = item
					}
				}
			}
			return consolidatedMap.values.toList()
		}
}
