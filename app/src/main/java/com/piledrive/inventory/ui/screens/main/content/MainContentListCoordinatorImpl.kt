package com.piledrive.inventory.ui.screens.main.content

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewLocalizedContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ListItemOverflowMenuCoordinator
import kotlinx.coroutines.flow.StateFlow

interface MainContentListCoordinatorImpl {
	val stashContentFlow: StateFlow<LocalizedContentState>
	val locationState: StateFlow<LocationContentState>
	val tagState: StateFlow<TagsContentState>
	val itemMenuCoordinator: ListItemOverflowMenuCoordinator
	val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit
	fun startStashTransfer(item: Item, startingLocation: Location?)
}

val stubMainContentListCoordinator = object : MainContentListCoordinatorImpl {
	override val stashContentFlow: StateFlow<LocalizedContentState> = previewLocalizedContentFlow()
	override val locationState: StateFlow<LocationContentState> = previewLocationContentFlow()
	override val tagState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val itemMenuCoordinator: ListItemOverflowMenuCoordinator = ListItemOverflowMenuCoordinator()
	override val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit = { _, _ -> }
	override fun startStashTransfer(item: Item, startingLocation: Location?) {}
}

class MainContentListCoordinator(
	override val stashContentFlow: StateFlow<LocalizedContentState>,
	override val locationState: StateFlow<LocationContentState>,
	override val tagState: StateFlow<TagsContentState>,
	override val itemMenuCoordinator: ListItemOverflowMenuCoordinator,
	override val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit,
	private val onStartStashTransfer: (item: Item, startingLocation: Location?) -> Unit,
) : ListItemOverflowMenuCoordinator(), MainContentListCoordinatorImpl {
	override fun startStashTransfer(item: Item, startingLocation: Location?) {
		onDismiss()
		onStartStashTransfer(item, startingLocation)
	}
}