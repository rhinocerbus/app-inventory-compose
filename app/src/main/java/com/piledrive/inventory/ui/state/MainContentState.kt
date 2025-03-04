package com.piledrive.inventory.ui.state

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.Stock
import com.piledrive.inventory.data.model.Tag


//  region location filter
/////////////////////////////////////////////////

data class LocationOptions(
	val allLocations: List<Location> = listOf(defaultLocation),
	val userLocations: List<Location> = listOf(),
	val currentLocation: Location = defaultLocation
) {
	companion object {
		val defaultLocation = Location(STATIC_ID_LOCATION_ALL, "", "Everywhere")
	}
}

data class LocationContentState(
	override val data: LocationOptions = LocationOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion


//  region tag filter
/////////////////////////////////////////////////

data class TagOptions(
	val allTags: List<Tag> = listOf(defaultTag),
	val userTags: List<Tag> = listOf(),
	val currentTag: Tag = defaultTag
) {
	companion object {
		private val defaultTag = Tag("", "", "Everything")
	}
}

data class TagsContentState(
	override val data: TagOptions = TagOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion


//  region by-location items content
/////////////////////////////////////////////////

data class ItemStockOptions(
	val itemStocks: List<Stock> = listOf(),
)

data class ItemContentState(
	override val data: ItemStockOptions = ItemStockOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion
