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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface MainFilterAppBarCoordinatorImpl {
	val locationsSourceFlow: StateFlow<LocationContentState>
	val tagsSourceFlow: StateFlow<TagsContentState>
	val tagsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Tag>
	val locationsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location>
	val sortDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<SortOrder>
	val sortDescendingState: StateFlow<Boolean>
	fun toggleSortOrder(sortDesc: Boolean)
}

class MainFilterAppBarCoordinator(
	override val locationsSourceFlow: StateFlow<LocationContentState>,
	override val tagsSourceFlow: StateFlow<TagsContentState>,
	override val tagsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Tag>,
	override val locationsDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<Location>,
	override val sortDropdownCoordinator: ReadOnlyDropdownCoordinatorGeneric<SortOrder>,
	sortDesc: Boolean
) : MainFilterAppBarCoordinatorImpl {
	private val _sortDesc = MutableStateFlow(sortDesc)
	override val sortDescendingState: StateFlow<Boolean> = _sortDesc

	override fun toggleSortOrder(sortDesc: Boolean) {
		_sortDesc.value = sortDesc
		// need to trigger data refresh. most straight-forward way currently
		sortDropdownCoordinator.onOptionSelected(sortDropdownCoordinator.selectedOptionState.value)
	}
}

val stubMainFilterAppBarCoordinator = MainFilterAppBarCoordinator(
	locationsSourceFlow = previewLocationContentFlow(),
	tagsSourceFlow = previewTagsContentFlow(),
	tagsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
	locationsDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
	sortDropdownCoordinator = ReadOnlyDropdownCoordinatorGeneric(),
	sortDesc = false
)

