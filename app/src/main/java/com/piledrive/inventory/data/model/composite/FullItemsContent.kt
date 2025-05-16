package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.abstracts.FullDataModel

data class FullItemData(val item: Item, val unit: QuantityUnit, val tags: List<Tag>) : FullDataModel

data class FullItemsContent(
	val fullItems: List<FullItemData> = listOf()
) {

	companion object {
		fun generateSampleSet(): FullItemsContent {
			val locationId = "l1"
			val items = listOf<FullItemData>(
				FullItemData(
					item = Item(
						id = "i1",
						createdAt = "",
						name = "item 1",
						unitId = QuantityUnit.defaultUnitBags.id
					),
					unit = QuantityUnit.defaultUnitBags,
					tags = listOf(
						Tag(id = "t1", createdAt = "", name = "meat"),
						Tag(id = "t2", createdAt = "", name = "leftovers"),
					),
				),

				FullItemData(
					item = Item(
						id = "i2",
						createdAt = "",
						name = "item 2",
						unitId = QuantityUnit.defaultUnitBags.id
					),
					unit = QuantityUnit.defaultUnitBags,
					tags = listOf(
						Tag(id = "t1", createdAt = "", name = "meat"),
						Tag(id = "t2", createdAt = "", name = "leftovers"),
					),
				)
			)
			val content = FullItemsContent(items)
			return content
		}
	}
}
