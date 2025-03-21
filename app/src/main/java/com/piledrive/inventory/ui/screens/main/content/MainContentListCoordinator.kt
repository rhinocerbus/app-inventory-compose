package com.piledrive.inventory.ui.screens.main.content

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.util.previewLocalizedContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ListItemOverflowMenuCoordinator
import kotlinx.coroutines.flow.StateFlow

class MainContentListCoordinator(
	val stashContentFlow: StateFlow<LocalizedContentState> = previewLocalizedContentFlow(),
	val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit = { _, _ -> },
	private val onStartStashTransfer: (item: Item, startingLocation: Location?) -> Unit = { _, _ -> }
) : ListItemOverflowMenuCoordinator() {
	fun startStashTransfer(item: Item, startingLocation: Location?) {
		onDismiss()
		onStartStashTransfer(item, startingLocation)
	}
}