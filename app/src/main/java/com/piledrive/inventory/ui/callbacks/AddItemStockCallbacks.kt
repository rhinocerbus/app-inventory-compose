package com.piledrive.inventory.ui.callbacks

import com.piledrive.inventory.data.model.StockSlug

interface AddItemStockCallbacks {
	//val onShowAdd: (startingLocation: Location?) -> Unit
	val onAddItemToLocation: (slug: StockSlug) -> Unit
}

val stubAddItemStockCallbacks = object : AddItemStockCallbacks {
	//override val onShowAdd: (startingLocation: Location?) -> Unit = {}
	override val onAddItemToLocation: (slug: StockSlug) -> Unit = { }
}
