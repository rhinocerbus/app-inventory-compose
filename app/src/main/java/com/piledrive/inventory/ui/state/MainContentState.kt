package com.piledrive.inventory.ui.state

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.STATIC_ID_LOCATION_ALL
import com.piledrive.inventory.data.model.STATIC_ID_TAG_ALL
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.composite.ContentForLocation
import com.piledrive.inventory.data.model.composite.ItemWithTagsContent


//  region location filter
/////////////////////////////////////////////////

data class LocationOptions(
	val allLocations: List<Location> = listOf(defaultLocation),
	val userLocations: List<Location> = listOf(),
	val currentLocation: Location = defaultLocation
) {
	companion object {
		val defaultLocation = Location(STATIC_ID_LOCATION_ALL, "", "Everywhere")
		fun generateSampleSet(): List<Location> {
			return listOf(
				Location(id = "0", createdAt = "0", name = "Fridge"),
				Location(id = "1", createdAt = "1", name = "Freezer"),
			)
		}
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
	val userTags: List<Tag> = listOf(),
	val currentTag: Tag = defaultTag
) {

	val allTags: List<Tag>
		get() = listOf(defaultTag, *userTags.toTypedArray())

	companion object {
		val defaultTag = Tag(STATIC_ID_TAG_ALL, "", "Everything")
	}
}

data class TagsContentState(
	override val data: TagOptions = TagOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion


//  region quantity units
/////////////////////////////////////////////////

data class QuantityUnitOptions(
	val customUnits: List<QuantityUnit> = listOf()
) {
	val allUnits: List<QuantityUnit>
		get() = QuantityUnit.defaultSet + customUnits
}

data class QuantityUnitContentState(
	override val data: QuantityUnitOptions = QuantityUnitOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion


//  region item stashes
/////////////////////////////////////////////////

data class ItemStashOptions(
	val itemStashes: List<Stash> = listOf(),
)

data class ItemStashContentState(
	override val data: ItemStashOptions = ItemStashOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion


//  region by-location items content
/////////////////////////////////////////////////

data class LocalizedContentState(
	override val data: ContentForLocation = ContentForLocation(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion


//  region by-location items content
/////////////////////////////////////////////////

data class FullItemsContentState(
	override val data: ItemWithTagsContent = ItemWithTagsContent(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion


//  region items content
/////////////////////////////////////////////////

data class ItemOptions(
	val items: List<Item> = listOf(),
) {
	companion object {
		fun generateSampleSet(): List<Item> {
			return listOf(
				Item(id = "0", createdAt = "", name = "Apples", unitId = QuantityUnit.DEFAULT_ID_BAGS),
				Item(id = "1", createdAt = "", name = "Bananas", unitId = QuantityUnit.DEFAULT_ID_BAGS),
				Item(id = "2", createdAt = "", name = "Coffee", unitId = QuantityUnit.DEFAULT_ID_BAGS),
				Item(id = "3", createdAt = "", name = "Donuts", unitId = QuantityUnit.DEFAULT_ID_BAGS),
				Item(id = "4", createdAt = "", name = "Earwigs", unitId = QuantityUnit.DEFAULT_ID_BAGS),
			)
		}
	}
}

data class ItemContentState(
	override val data: ItemOptions = ItemOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

/////////////////////////////////////////////////
//  endregion
