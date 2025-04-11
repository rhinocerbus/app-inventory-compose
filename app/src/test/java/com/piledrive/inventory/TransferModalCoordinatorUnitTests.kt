package com.piledrive.inventory

import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.ui.modal.transfer_item.TransferItemStashSheetCoordinator
import com.piledrive.inventory.ui.state.ItemOptions
import com.piledrive.inventory.ui.state.LocationOptions
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import com.piledrive.inventory.ui.util.previewQuantityUnitsContentFlow
import org.junit.Test

/**
 * Unit tests specifically for Coordinator class that drives modal for moving amounts
 * between stashes - in its own file since it doesn't neatly fit the same interfaces as the others.
 */
class TransferModalCoordinatorUnitTests {

	@Test
	fun test_full_transfer_flow() {
		val sampleItems = ItemOptions.generateSampleSet()
		val sampleLocation = LocationOptions.generateSampleSet()
		val sampleStashes = sampleItems.mapIndexed { idx, item ->
			Stash(id = "$idx", createdAt = "", itemId = item.id, locationId = sampleLocation[0].id, amount = (idx + 1) * 1.5)
		}
		val coordinator = TransferItemStashSheetCoordinator(
			itemsSourceFlow = previewItemsContentFlow(sampleItems),
			unitsSourceFlow = previewQuantityUnitsContentFlow(),
			locationsSourceFlow = previewLocationContentFlow(sampleLocation),
			stashesSourceFlow = previewItemStashesContentFlow(sampleStashes),
			onCommitStashTransfer = { _, _ -> }
		)

		val targetItem = sampleItems[0]
		coordinator.showSheetForItem(targetItem)
		assert(coordinator.showSheetState.value)
		assert(coordinator.activeItemState.value == targetItem)
		assert(coordinator.amountDifference.value == 0.0)
		assert(coordinator.fromLocationDropdownCoordinator.selectedOptionState.value == null)
		assert(coordinator.toLocationDropdownCoordinator.selectedOptionState.value == null)
		val fromStashes = coordinator.fromLocationDropdownCoordinator.dropdownOptionsState.value
		val toStashes = coordinator.toLocationDropdownCoordinator.dropdownOptionsState.value
		assert(fromStashes.isNotEmpty())
		assert(toStashes.isNotEmpty())

		val fromStash = fromStashes[0]
		val toStash = toStashes.last()
		coordinator.fromLocationDropdownCoordinator.onOptionSelected(fromStash)
		coordinator.toLocationDropdownCoordinator.onOptionSelected(toStash)
		assert(coordinator.fromLocationDropdownCoordinator.selectedOptionState.value == fromStash)
		assert(coordinator.toLocationDropdownCoordinator.selectedOptionState.value == toStash)

		coordinator.changeTransferAmount(-1.0)
		assert(coordinator.amountDifference.value == 0.0)
		coordinator.changeTransferAmount(fromStash.stash.amount * 100)
		assert(coordinator.amountDifference.value == fromStash.stash.amount)
		val validAmount = fromStash.stash.amount * 0.5
		coordinator.changeTransferAmount(validAmount)
		assert(coordinator.amountDifference.value == validAmount)

		coordinator.onCommitStashTransfer(fromStash.stash, toStash.stash)
		assert(!coordinator.showSheetState.value)
	}
}