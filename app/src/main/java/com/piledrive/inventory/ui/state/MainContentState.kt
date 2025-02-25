package com.piledrive.inventory.ui.state

import com.piledrive.inventory.model.Item
import com.piledrive.inventory.model.Location

data class LocationContentState(
	override val data: List<Location> = listOf()
) : GenericContentState()

data class ItemContentState(
	override val data: List<Item> = listOf()
) : GenericContentState()