package com.piledrive.inventory.ui.bars

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.TagsContentState
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.MenuCoordinator
import kotlinx.coroutines.flow.StateFlow


interface MainFilterAppBarCoordinatorImpl {
	val locationState: StateFlow<LocationContentState>
	val tagState: StateFlow<TagsContentState>
	val onLocationChanged: (loc: Location) -> Unit
	val onTagChanged: (tag: Tag) -> Unit
	val locationMenuCoordinator: MenuCoordinator
	val tagMenuCoordinator: MenuCoordinator
}

class MainFilterAppBarCoordinator(
	override val locationState: StateFlow<LocationContentState>,
	override val tagState: StateFlow<TagsContentState>,
	override val onLocationChanged: (loc: Location) -> Unit,
	override val onTagChanged: (tag: Tag) -> Unit,
	override val locationMenuCoordinator: MenuCoordinator,
	override val tagMenuCoordinator: MenuCoordinator
) : MainFilterAppBarCoordinatorImpl

val stubMainFilterAppBarCoordinator = object : MainFilterAppBarCoordinatorImpl {
	override val locationState: StateFlow<LocationContentState> = previewLocationContentFlow()
	override val tagState: StateFlow<TagsContentState> = previewTagsContentFlow()
	override val onLocationChanged: (loc: Location) -> Unit = {}
	override val onTagChanged: (tag: Tag) -> Unit = {}
	override val locationMenuCoordinator: MenuCoordinator = MenuCoordinator()
	override val tagMenuCoordinator: MenuCoordinator = MenuCoordinator()
}

