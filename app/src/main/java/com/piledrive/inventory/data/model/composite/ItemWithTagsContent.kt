package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Tag

data class ItemWithTagsContent(
	val fullItems: List<ItemWithTags> = listOf()
) {

	companion object {
		fun generateSampleSet(): ItemWithTagsContent {
			val locationId = "l1"
			val items = listOf<ItemWithTags>(
				ItemWithTags(
					item = ItemWithUnit(
						item = Item(
							id = "i1",
							createdAt = "",
							name = "item 1",
							unitId = QuantityUnit.defaultUnitBags.id
						),
						unit = QuantityUnit.defaultUnitBags
					),
					tags = listOf(
						Tag(id = "t1", createdAt = "", name = "meat"),
						Tag(id = "t2", createdAt = "", name = "leftovers"),
					),
				),

				ItemWithTags(
					item = ItemWithUnit(
						item = Item(
							id = "i2",
							createdAt = "",
							name = "item 2",
							unitId = QuantityUnit.defaultUnitBags.id
						),
						unit = QuantityUnit.defaultUnitBags
					),
					tags = listOf(
						Tag(id = "t1", createdAt = "", name = "meat"),
						Tag(id = "t2", createdAt = "", name = "leftovers"),
					),
				)
			)
			val content = ItemWithTagsContent(items)
			return content
		}
	}
}
