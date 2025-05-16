package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag

data class FullStashData(val stash: Stash, val location: Location)

data class FullStashesContent(
	val fullStashes: List<FullStashData> = listOf()
) {

	companion object {
		fun generateSampleSet(): FullStashesContent {
			val data = listOf<FullStashData>(
				FullStashData(
					stash = Stash(
						id = "s1",
						createdAt = "",
						itemId = "i1",
						locationId = "l1",
						amount = 1.0
					),
					location = Location(
						id = "l1",
						createdAt = "",
						name = "Freezer"
					)
				),
				FullStashData(
					stash = Stash(
						id = "s2",
						createdAt = "",
						itemId = "i2",
						locationId = "l2",
						amount = 1.0
					),
					location = Location(
						id = "l2",
						createdAt = "",
						name = "Pantry"
					)
				)
			)
			val content = FullStashesContent(data)
			return content
		}
	}
}