package com.piledrive.inventory.ui.callbacks

import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.Tag

interface CreateItemCallbacks {
	//val onShowCreate: () -> Unit
	val onAddItem: (item: ItemSlug) -> Unit
}

val stubCreateItemCallbacks = object : CreateItemCallbacks {
	//override val onShowCreate: () -> Unit = {}
	override val onAddItem: (item: ItemSlug) -> Unit = { }
}
