package com.piledrive.inventory.ui.callbacks

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Stock

interface AddItemStockCallbacks {
	val onShowAdd: (startingLocation: Location?) -> Unit
	val onAddItemToLocation: (itemStock: Stock, location: Location) -> Unit
}

val stubAddItemStockCallbacks = object : AddItemStockCallbacks {
	override val onShowAdd: (startingLocation: Location?) -> Unit = {}
	override val onAddItemToLocation: (itemStock: Stock, location: Location) -> Unit = { _, _ -> }
}
