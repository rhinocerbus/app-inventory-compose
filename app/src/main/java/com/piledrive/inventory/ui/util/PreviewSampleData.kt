package com.piledrive.inventory.ui.util

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.composite.StashesForItem
import com.piledrive.inventory.data.model.composite.ItemWithTagsContent
import com.piledrive.inventory.ui.state.FullItemsContentState
import com.piledrive.inventory.ui.state.ItemContentState
import com.piledrive.inventory.ui.state.ItemOptions
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.ItemStashOptions
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocalizedStashesPayload
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import com.piledrive.inventory.ui.state.QuantityUnitOptions
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.state.TagsContentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


fun previewLocationContentFlow(
	locations: List<Location> = listOf()
): StateFlow<LocationContentState> {
	return MutableStateFlow(
		LocationContentState(
			data = LocationOptions(userLocations = locations),
			hasLoaded = true,
			isLoading = false
		)
	)
}

fun previewTagsContentFlow(
	tags: List<Tag> = listOf()
): StateFlow<TagsContentState> {
	return MutableStateFlow(
		TagsContentState(
			data = TagOptions(userTags = tags),
			hasLoaded = true,
			isLoading = false
		)
	)
}

fun previewQuantityUnitsContentFlow(
): StateFlow<QuantityUnitContentState> {
	return MutableStateFlow(QuantityUnitContentState(hasLoaded = true, isLoading = false))
}

fun previewItemsContentFlow(
	items: List<Item> = ItemOptions.generateSampleSet()
): StateFlow<ItemContentState> {
	return MutableStateFlow(ItemContentState(data = ItemOptions(items), hasLoaded = true, isLoading = false))
}

fun previewUnitsContentFlow(
	units: List<QuantityUnit> = listOf()
): StateFlow<QuantityUnitContentState> {
	return MutableStateFlow(QuantityUnitContentState(data = QuantityUnitOptions(units), hasLoaded = true, isLoading = false))
}

fun previewFullItemsContentFlow(
	dataSet: ItemWithTagsContent = ItemWithTagsContent.generateSampleSet()
): StateFlow<FullItemsContentState> {
	return MutableStateFlow(FullItemsContentState(data = dataSet, hasLoaded = true, isLoading = false))
}

fun previewItemStashesContentFlow(
	dataSet: List<Stash> = listOf()
): StateFlow<ItemStashContentState> {
	return MutableStateFlow(ItemStashContentState(data = ItemStashOptions(dataSet), hasLoaded = true, isLoading = false))
}

fun previewLocalizedContentFlow(
): StateFlow<LocalizedContentState> {
	return MutableStateFlow(LocalizedContentState(hasLoaded = true, isLoading = false))
}

fun previewLocalizedContentState(
): State<LocalizedContentState> {
	return mutableStateOf(LocalizedContentState(hasLoaded = true, isLoading = false))
}

fun previewLocalizedContentFlowWithData(
): StateFlow<LocalizedContentState> {
	return MutableStateFlow(
		LocalizedContentState(
			data = LocalizedStashesPayload(StashesForItem.generateSampleSet()),
			hasLoaded = true,
			isLoading = false
		)
	)
}