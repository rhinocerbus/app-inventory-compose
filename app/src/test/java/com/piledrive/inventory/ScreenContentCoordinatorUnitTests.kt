package com.piledrive.inventory

import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.screens.items.content.ManageItemsContentCoordinator
import com.piledrive.inventory.ui.screens.tags.content.ManageTagsContentCoordinator
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.util.previewFullItemsContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import org.junit.Test

/**
 * Unit tests for Coordinator classes that drive the functionality for pieces of UI, mainly screens & modals.
 * This provides both a smaller parameter footprint for composables and makes callbacks & state interactions testable without
 * actual UI testing.
 */
class ScreenContentCoordinatorUnitTests {

	@Test
	fun manage_tags_coordinator_actions_tests() {
		val sampleTag = TagOptions.defaultTag
		val sampleTags = listOf(sampleTag)
		val coordinator = ManageTagsContentCoordinator(
			tagState = previewTagsContentFlow(sampleTags),
			createTagCoordinator = CreateTagSheetCoordinator(
				previewTagsContentFlow(sampleTags),
				onAddTag = {},
				onUpdateTag = {}
			)
		)
		assert(coordinator.tagState.value.data.userTags == sampleTags)
		coordinator.onLaunchCreateTag()
		assert(coordinator.createTagCoordinator.showSheetState.value)
		assert(coordinator.createTagCoordinator.activeEditDataState.value == null)
		coordinator.onTagClicked(sampleTag)
		assert(coordinator.createTagCoordinator.showSheetState.value)
		assert(coordinator.createTagCoordinator.activeEditDataState.value == sampleTag)
	}
}