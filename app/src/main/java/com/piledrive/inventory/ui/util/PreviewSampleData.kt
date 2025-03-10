package com.piledrive.inventory.ui.util

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.composite.ContentForLocation
import com.piledrive.inventory.data.model.composite.StashForItem
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import com.piledrive.inventory.ui.state.TagsContentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


fun previewLocationContentFlow(
	locations: List<Location> = listOf()
): StateFlow<LocationContentState> {
	return MutableStateFlow(LocationContentState(data = LocationOptions(userLocations = locations), hasLoaded = true, isLoading = false))
}

fun previewTagsContentFlow(
): StateFlow<TagsContentState> {
	return MutableStateFlow(TagsContentState(hasLoaded = true, isLoading = false))
}

fun previewItemsContentFlow(
): StateFlow<ItemContentState> {
	return MutableStateFlow(ItemContentState(hasLoaded = true, isLoading = false))
}

fun previewItemStashesContentFlow(
): StateFlow<ItemStashContentState> {
	return MutableStateFlow(ItemStashContentState(hasLoaded = true, isLoading = false))
}

fun previewLocalizedContentFlow(
): StateFlow<LocalizedContentState> {
	return MutableStateFlow(LocalizedContentState(hasLoaded = true, isLoading = false))
}

fun previewLocalizedContentFlowWithData(
): StateFlow<LocalizedContentState> {
	return MutableStateFlow(LocalizedContentState(data = ContentForLocation.generateSampleSet(), hasLoaded = true, isLoading = false))
}