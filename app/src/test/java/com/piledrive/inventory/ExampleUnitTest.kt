package com.piledrive.inventory

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.util.previewLocationContentFlow
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
	@Test
	fun modal_coordinator_display_state_tests() {
		val coordinator = CreateLocationModalSheetCoordinator(
			locationState = previewLocationContentFlow(),
			onAddLocation = {},
			onUpdateLocation = {}
		)
		assert(!coordinator.showSheetState.value)
		assert(coordinator.activeEditDataState.value == null)
		coordinator.showSheet()
		assert(coordinator.showSheetState.value)
		assert(coordinator.activeEditDataState.value == null)
		coordinator.onDismiss()
		assert(!coordinator.showSheetState.value)
		coordinator.showSheetWithData(Location(id = "", createdAt = "", name = ""))
		assert(coordinator.showSheetState.value)
		assert(coordinator.activeEditDataState.value != null)
		coordinator.onDismiss()
		assert(!coordinator.showSheetState.value)
	}
}