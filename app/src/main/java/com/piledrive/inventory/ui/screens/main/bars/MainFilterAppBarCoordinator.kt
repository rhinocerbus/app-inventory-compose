package com.piledrive.inventory.ui.screens.main.bars

import com.piledrive.inventory.data.enums.SortOrder
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import com.piledrive.lib_compose_components.ui.util.previewBooleanFlow
import kotlinx.coroutines.flow.StateFlow


interface MainFilterAppBarCoordinatorImpl {
	val locationState: StateFlow<LocationContentState>
	val tagState: StateFlow<TagsContentState>
	val tagsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Tag>
	val locationsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location>
	val sortDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<SortOrder>
	val sortDescendingState: StateFlow<Boolean>
}

class MainFilterAppBarCoordinator(
	override val locationState: StateFlow<LocationContentState>,
	override val tagState: StateFlow<TagsContentState>,
	override val tagsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Tag>,
	override val locationsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location>,
	override val sortDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<SortOrder>,
	override val sortDescendingState: StateFlow<Boolean>
) : MainFilterAppBarCoordinatorImpl {
}

val stubMainFilterAppBarCoordinator = object : MainFilterAppBarCoordinatorImpl {
	override val locationState: StateFlow<LocationContentState> = previewLocationContentFlow()
	override val tagState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val tagsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Tag> = ReadOnlyDropdownCoordinatorGeneric()
	override val locationsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location> =
		ReadOnlyDropdownCoordinatorGeneric()
	override val sortDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<SortOrder> = ReadOnlyDropdownCoordinatorGeneric()
	override val sortDescendingState: StateFlow<Boolean> = previewBooleanFlow(false)
}

