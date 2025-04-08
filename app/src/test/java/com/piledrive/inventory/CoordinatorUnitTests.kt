package com.piledrive.inventory

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityType
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.ui.modal.coordinators.EditableDataModalCoordinatorImpl
import com.piledrive.inventory.ui.modal.create_item.CreateItemSheetCoordinator
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewQuantityUnitsContentFlow
import com.piledrive.inventory.ui.util.previewTagsContentFlow
import com.piledrive.lib_compose_components.ui.coordinators.ModalSheetCoordinator
import org.junit.Test

/**
 * Unit tests for Coordinator classes that drive the functionality for pieces of UI, mainly screens & modals.
 * This provides both a smaller parameter footprint for composables and makes callbacks & state interactions testable without
 * actual UI testing.
 */
class CoordinatorUnitTests {
	@Test
	fun item_modal_coordinator_display_state_tests() {
		val coordinator = CreateItemSheetCoordinator(
			itemState = previewItemsContentFlow(),
			quantityContentState = previewQuantityUnitsContentFlow(),
			tagsContentState = previewTagsContentFlow(),
			onAddItem = {},
			onUpdateItem = { _, _ -> },
			onLaunchAddUnit = {},
			onLaunchAddTag = {},
		)
		val stubItem = ItemWithTags(
			item = Item(id = "", createdAt = "", name = "", unitId = ""),
			tags = listOf(),
			quantityUnit = QuantityUnit.defaultUnitBags
		)
		generic_modal_coordinator_display_state_tests(coordinator, coordinator, stubItem)
	}

	@Test
	fun tag_modal_coordinator_display_state_tests() {
		val coordinator = CreateTagSheetCoordinator(
			tagsContentState = previewTagsContentFlow(),
			onAddTag = {},
			onUpdateTag = {},
		)
		val stubTag = Tag(id = "", createdAt = "", name = "")
		generic_modal_coordinator_display_state_tests(coordinator, coordinator, stubTag)
	}

	@Test
	fun unit_modal_coordinator_display_state_tests() {
		val coordinator = CreateQuantityUnitSheetCoordinator(
			unitsContentState = previewQuantityUnitsContentFlow(),
			onAddQuantityUnit = {},
			onUpdateQuantityUnit = {},
		)
		val stubUnit = QuantityUnit(id = "", createdAt = "", name = "", label = "", type = QuantityType.WHOLE)
		generic_modal_coordinator_display_state_tests(coordinator, coordinator, stubUnit)
	}

	@Test
	fun location_modal_coordinator_display_state_tests() {
		val coordinator = CreateLocationModalSheetCoordinator(
			locationState = previewLocationContentFlow(),
			onAddLocation = {},
			onUpdateLocation = {}
		)
		val stubLocation = Location(id = "", createdAt = "", name = "")
		generic_modal_coordinator_display_state_tests(coordinator, coordinator, stubLocation)
	}

	private fun <T> generic_modal_coordinator_display_state_tests(
		modalCoordinator: ModalSheetCoordinator,
		editableDataModalCoordinatorImpl: EditableDataModalCoordinatorImpl<T>,
		stubData: T
	) {
		assert(modalCoordinator == editableDataModalCoordinatorImpl)

		assert(!modalCoordinator.showSheetState.value)
		assert(editableDataModalCoordinatorImpl.activeEditDataState.value == null)
		modalCoordinator.showSheet()
		assert(modalCoordinator.showSheetState.value)
		assert(editableDataModalCoordinatorImpl.activeEditDataState.value == null)
		modalCoordinator.onDismiss()
		assert(!modalCoordinator.showSheetState.value)
		editableDataModalCoordinatorImpl.showSheetWithData(stubData)
		assert(modalCoordinator.showSheetState.value)
		assert(editableDataModalCoordinatorImpl.activeEditDataState.value != null)
		modalCoordinator.onDismiss()
		assert(!modalCoordinator.showSheetState.value)
	}
}