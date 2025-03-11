package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag

data class ContentForLocation(
	val locationId: String = STATIC_ID_LOCATION_ALL,
	val currentLocationItemStashContent: List<StashForItem> = listOf()
) {

	companion object {
		fun generateSampleSet(): ContentForLocation {
			val locationId = "l1"
			val stashes = listOf<StashForItem>(
				StashForItem(
					Stash(
						id = "s1",
						createdAt = "",
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
			val content = ContentForLocation(locationId, stashes)
			return content
		}
	}
}
