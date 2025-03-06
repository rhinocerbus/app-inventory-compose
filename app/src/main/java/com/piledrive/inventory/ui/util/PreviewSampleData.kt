package com.piledrive.inventory.ui.util

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemStockContentState
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

fun previewItemStocksContentFlow(
): StateFlow<ItemStockContentState> {
	return MutableStateFlow(ItemStockContentState(hasLoaded = true, isLoading = false))
}
