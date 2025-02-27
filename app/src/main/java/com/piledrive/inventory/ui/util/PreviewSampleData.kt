package com.piledrive.inventory.ui.util

import com.piledrive.inventory.ui.state.LocationContentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


fun previewMainContentFlow(
): StateFlow<LocationContentState> {
	return MutableStateFlow(LocationContentState(hasLoaded = true, isLoading = false))
}
