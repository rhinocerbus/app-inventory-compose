package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag
import java.lang.IllegalStateException

data class StashesForItem(
	val item: FullItemData,
	val stashes: List<FullStashData>
) {

	val stash: Stash
		get() {
			if(stashes.size > 1) {
				throw IllegalStateException("single-stash accessor used when multiple stashes present, use indexed list to ensure correctness at call-site")
			}
			return stashes[0].stash
		}

	val totalAmount: Double
		get() {
			var amount = 0.0
			stashes.forEach { amount += it.stash.amount }
			return amount
		}

	val isSingleStash: Boolean
		get() = stashes.size == 1

	companion object {
		fun generateSampleSet(): List<StashesForItem> {
			val tag1 = Tag(id = "t1", createdAt = "", name = "meat")
			val tag2 = Tag(id = "t2", createdAt = "", name = "leftovers")

			val item1 = FullItemData(
				item = Item(
					id = "i1",
					createdAt = "",
					name = "item 1",
					unitId = QuantityUnit.defaultUnitBags.id
				),
				unit = QuantityUnit.defaultUnitBags,
				tags = listOf(tag1)
			)

			val item2 = FullItemData(
				item = Item(
						id = "i2",
						createdAt = "",
						name = "item 2",
						unitId = QuantityUnit.defaultUnitBags.id
				),
				unit = QuantityUnit.defaultUnitBags,
				tags = listOf(tag1, tag2)
			)

			val location1 = Location(id = "l1", createdAt = "", name = "Freezer")
			val location2 = Location(id = "l2", createdAt = "", name = "Pantry")

			val stashes = listOf<StashesForItem>(
				StashesForItem(
					item = item1,
					stashes = listOf(
						FullStashData(
							stash = Stash(
								id = "s1",
								createdAt = "",
								itemId = item1.item.id,
								amount = 99.99,
								locationId = location1.id
							),
							location = location1
						),
						FullStashData(
							stash = Stash(
								id = "s2",
								createdAt = "",
								itemId = item1.item.id,
								amount = 33.33,
								locationId = location2.id
							),
							location = location2
						),
					)
				),
				StashesForItem(
					item = item2,
					stashes = listOf(
						FullStashData(
							stash = Stash(
								id = "s3",
								createdAt = "",
								itemId = item2.item.id,
								amount = 22.0,
								locationId = location1.id
							),
							location = location1
						),
						FullStashData(
							stash = Stash(
								id = "s4",
								createdAt = "",
								itemId = item2.item.id,
								amount = 44.0,
								locationId = location2.id
							),
							location = location2
						),
					)
				)
			)
			return stashes
		}
	}
}
