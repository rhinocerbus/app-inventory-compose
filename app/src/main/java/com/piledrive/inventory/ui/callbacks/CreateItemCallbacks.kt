package com.piledrive.inventory.ui.callbacks

import com.piledrive.inventory.data.model.Tag

interface CreateItemCallbacks {
	//val onShowCreate: () -> Unit
	val onAddItem: (name: String, tags: List<Tag>) -> Unit
}

val stubCreateItemCallbacks = object : CreateItemCallbacks {
	//override val onShowCreate: () -> Unit = {}
	override val onAddItem: (name: String, tags: List<Tag>) -> Unit = { _, _ -> }
}
