package com.piledrive.inventory.ui.state

import com.piledrive.inventory.model.Item
import com.piledrive.inventory.model.Location
import com.piledrive.inventory.model.Tag

data class LocationOptions(
	val allLocations: List<Location> = listOf(defaultLocation),
	val userLocations: List<Location> = listOf(),
	val currentLocation: Location = defaultLocation
) {
	companion object {
		private val defaultLocation = Location("", "All", "")
	}
}

data class LocationContentState(
	override val data: LocationOptions = LocationOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()


data class TagOptions(
	val allTags: List<Tag> = listOf(defaultTag),
	val userTags: List<Tag> = listOf(),
	val currentTag: Tag = defaultTag
) {
	companion object {
		private val defaultTag = Tag("All")
	}
}

data class TagsContentState(
	override val data: TagOptions = TagOptions(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()

data class ItemContentState(
	override val data: List<Item> = listOf(),
	override val hasLoaded: Boolean = false,
	override val isLoading: Boolean = true
) : GenericContentState()