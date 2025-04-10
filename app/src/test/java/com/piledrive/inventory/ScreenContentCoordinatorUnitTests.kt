package com.piledrive.inventory

import com.piledrive.inventory.data.model.composite.FullItemsContent
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.stubCreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.stubCreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.screens.items.content.ManageItemsContentCoordinator
import com.piledrive.inventory.ui.screens.locations.content.ManageLocationsContentCoordinator
import com.piledrive.inventory.ui.screens.tags.content.ManageTagsContentCoordinator
import com.piledrive.inventory.ui.screens.units.content.ManageUnitsContentCoordinator
import com.piledrive.inventory.ui.state.LocationOptions
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.util.previewFullItemsContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.inventory.ui.util.previewUnitsContentFlow
import org.junit.Test

/**
 * Unit tests for Coordinator classes that drive the functionality for pieces of UI, mainly screens & modals.
 * This provides both a smaller parameter footprint for composables and makes callbacks & state interactions testable without
 * actual UI testing.
 */
class ScreenContentCoordinatorUnitTests {
	@Test
	fun manage_items_coordinator_actions_tests() {
		val sampleData = FullItemsContent.generateSampleSet()
		val coordinator = ManageItemsContentCoordinator(
			itemsSourceFlow = previewFullItemsContentFlow(sampleData),
			createItemCoordinator = CreateItemSheetCoordinator(
				itemsSourceFlow = previewItemsContentFlow(),
				unitsSourceFlow = previewUnitsContentFlow(),
				tagsSourceFlow = previewTagsContentFlow(),
				createTagCoordinator = stubCreateTagSheetCoordinator,
				createQuantityUnitSheetCoordinator = stubCreateQuantityUnitSheetCoordinator,
				onCreateDataModel = {},
				onUpdateDataModel = {}
			),
		)

		assert(coordinator.itemsSourceFlow.value.data.fullItems == sampleData.fullItems)
		coordinator.launchDataModelCreation()
		assert(coordinator.createItemCoordinator.showSheetState.value)
		assert(coordinator.createItemCoordinator.activeEditDataState.value == null)
		val targetItem = sampleData.fullItems[0]
		coordinator.launchDataModelEdit(targetItem)
		assert(coordinator.createItemCoordinator.showSheetState.value)
		assert(coordinator.createItemCoordinator.activeEditDataState.value == targetItem)
	}

	@Test
	fun manage_tags_coordinator_actions_tests() {
		val sampleTag = TagOptions.defaultTag
		val sampleTags = listOf(sampleTag)
		val coordinator = ManageTagsContentCoordinator(
			tagsSourceFlow = previewTagsContentFlow(sampleTags),
			createTagCoordinator = CreateTagSheetCoordinator(
				previewTagsContentFlow(sampleTags),
				onCreateDataModel = {},
				onUpdateDataModel = {}
			)
		)
		assert(coordinator.tagsSourceFlow.value.data.userTags == sampleTags)
		coordinator.launchDataModelCreation()
		assert(coordinator.createTagCoordinator.showSheetState.value)
		assert(coordinator.createTagCoordinator.activeEditDataState.value == null)
		coordinator.launchDataModelEdit(sampleTag)
		assert(coordinator.createTagCoordinator.showSheetState.value)
		assert(coordinator.createTagCoordinator.activeEditDataState.value == sampleTag)
	}

	@Test
	fun manage_locations_coordinator_actions_tests() {
		val sampleSet = LocationOptions.generateSampleSet()
		val sampleSource = previewLocationContentFlow(sampleSet)
		val coordinator = ManageLocationsContentCoordinator(
			locationsSourceFlow = sampleSource,
			createLocationCoordinator = CreateLocationModalSheetCoordinator(
				locationsSourceFlow = sampleSource,
				onCreateDataModel = {},
				onUpdateDataModel = {}
			)
		)
		assert(coordinator.locationsSourceFlow.value.data.userLocations == sampleSet)
		coordinator.launchDataModelCreation()
		assert(coordinator.createLocationCoordinator.showSheetState.value)
		assert(coordinator.createLocationCoordinator.activeEditDataState.value == null)
		val sampleLocation = sampleSet[0]
		coordinator.launchDataModelEdit(sampleLocation)
		assert(coordinator.createLocationCoordinator.showSheetState.value)
		assert(coordinator.createLocationCoordinator.activeEditDataState.value == sampleLocation)
	}

	@Test
	fun manage_units_coordinator_actions_tests() {
		val sampleSource = previewUnitsContentFlow()
		val sampleSet = sampleSource.value.data.allUnits
		val coordinator = ManageUnitsContentCoordinator(
			unitsSourceFlow = sampleSource,
			createQuantityUnitSheetCoordinator = CreateQuantityUnitSheetCoordinator(
				unitsSourceFlow = sampleSource,
				onCreateDataModel = {},
				onUpdateDataModel = {}
			),
		)
		assert(coordinator.unitsSourceFlow.value.data.allUnits == sampleSet)
		coordinator.launchDataModelCreation()
		assert(coordinator.createQuantityUnitSheetCoordinator.showSheetState.value)
		assert(coordinator.createQuantityUnitSheetCoordinator.activeEditDataState.value == null)
		val sampleUnit = sampleSet[0]
		coordinator.launchDataModelEdit(sampleUnit)
		assert(coordinator.createQuantityUnitSheetCoordinator.showSheetState.value)
		assert(coordinator.createQuantityUnitSheetCoordinator.activeEditDataState.value == sampleUnit)
	}
}