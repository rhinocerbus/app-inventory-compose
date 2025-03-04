package com.piledrive.inventory.ui.util

import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.TagsContentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


fun previewMainContentFlow(
): StateFlow<LocationContentState> {
	return MutableStateFlow(LocationContentState(hasLoaded = true, isLoading = false))
}

fun previewMainTagsFlow(
): StateFlow<TagsContentState> {
	return MutableStateFlow(TagsContentState(hasLoaded = true, isLoading = false))
}
