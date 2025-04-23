package com.piledrive.inventory

import com.piledrive.inventory.data.model.composite.StashesForItem
import com.piledrive.inventory.ui.modal.transfer_item.TransferItemStashSheetCoordinator
import com.piledrive.inventory.ui.util.previewItemStashesContentFlow
import com.piledrive.inventory.ui.util.previewItemsContentFlow
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import org.junit.Test

/**
 * Unit tests specifically for Coordinator class that drives modal for moving amounts
 * between stashes - in its own file since it doesn't neatly fit the same interfaces as the others.
 */
class TransferModalCoordinatorUnitTests {

	@Test
	fun test_full_transfer_flow() {
		val sampleData = StashesForItem.generateSampleSet()
		val sampleItems = sampleData.map { it.item.item }
		val sampleLocation = sampleData.flatMap { it.stashes.map { it.location } }
		val sampleStashes = sampleData.flatMap { it.stashes.map { it.stash } }
		val coordinator = TransferItemStashSheetCoordinator(
			itemsSourceFlow = previewItemsContentFlow(sampleItems),
			locationsSourceFlow = previewLocationContentFlow(sampleLocation),
			stashesSourceFlow = previewItemStashesContentFlow(sampleStashes),
			onCommitStashTransfer = { _, _ -> }
		)

		val targetItem = sampleData[0].item
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

		coordinator.submitTransfer()
		assert(!coordinator.showSheetState.value)
	}
}