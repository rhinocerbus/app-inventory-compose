package com.piledrive.inventory.ui.screens.main.content

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.composite.FullItemData
import com.piledrive.inventory.data.model.composite.StashesForItem
import com.piledrive.inventory.ui.state.LocalizedContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewLocalizedContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ListItemOverflowMenuCoordinator
import kotlinx.coroutines.flow.StateFlow

interface MainContentListCoordinatorImpl {
	val stashesSourceFlow: StateFlow<LocalizedContentState>
	val locationsSourceFlow: StateFlow<LocationContentState>
	val tagsSourceFlow: StateFlow<TagsContentState>
	val allLocationsSectionsCoordinator: SectionedListCoordinator
	val itemMenuCoordinator: ListItemOverflowMenuCoordinator
	val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit
	val onItemClicked: (itemStashes: StashesForItem) -> Unit
	fun startStashTransfer(item: FullItemData, startingLocation: Location?)
}

class MainContentListCoordinator(
	override val stashesSourceFlow: StateFlow<LocalizedContentState>,
	override val locationsSourceFlow: StateFlow<LocationContentState>,
	override val tagsSourceFlow: StateFlow<TagsContentState>,
	override val allLocationsSectionsCoordinator: SectionedListCoordinator,
	override val itemMenuCoordinator: ListItemOverflowMenuCoordinator,
	override val onItemStashQuantityUpdated: (stashId: String, qty: Double) -> Unit,
	override val onItemClicked: (itemStashes: StashesForItem) -> Unit,
	private val onStartStashTransfer: (item: FullItemData, startingLocation: Location?) -> Unit,
) : ListItemOverflowMenuCoordinator(), MainContentListCoordinatorImpl {
	override fun startStashTransfer(item: FullItemData, startingLocation: Location?) {
		onDismiss()
		onStartStashTransfer(item, startingLocation)
	}
}

val stubMainContentListCoordinator = MainContentListCoordinator(
	stashesSourceFlow = previewLocalizedContentFlow(),
	locationsSourceFlow = previewLocationContentFlow(),
	tagsSourceFlow = previewTagsContentFlow(),
	allLocationsSectionsCoordinator = SectionedListCoordinator(),
	itemMenuCoordinator = ListItemOverflowMenuCoordinator(),
	onItemStashQuantityUpdated = { _, _ -> },
	onItemClicked = {},
	onStartStashTransfer = { _, _ -> }
)