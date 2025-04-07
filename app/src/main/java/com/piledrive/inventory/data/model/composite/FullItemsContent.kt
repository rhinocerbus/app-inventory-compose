package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag

data class FullItemsContent(
	val fullItems: List<ItemWithTags> = listOf()
) {

	companion object {
		fun generateSampleSet(): FullItemsContent {
			val locationId = "l1"
			val items = listOf<ItemWithTags>(
				ItemWithTags(
					item = Item(
						id = "i1",
						createdAt = "",
						name = "item 1",
						unitId = QuantityUnit.defaultUnitBags.id
					),
					tags = listOf(
						Tag(id = "t1", createdAt = "", name = "meat"),
						Tag(id = "t2", createdAt = "", name = "leftovers"),
					),
					quantityUnit = QuantityUnit.defaultUnitBags
				),

				ItemWithTags(
					item = Item(
						id = "i2",
						createdAt = "",
						name = "item 2",
						unitId = QuantityUnit.defaultUnitBags.id
					),
					tags = listOf(
						Tag(id = "t1", createdAt = "", name = "meat"),
						Tag(id = "t2", createdAt = "", name = "leftovers"),
					),
					quantityUnit = QuantityUnit.defaultUnitBags
				)
			)
			val content = FullItemsContent(items)
			return content
		}
	}
}
