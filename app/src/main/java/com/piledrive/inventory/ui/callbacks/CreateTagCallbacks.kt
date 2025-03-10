package com.piledrive.inventory.ui.callbacks

interface CreateTagCallbacks {
	//val onShowCreate: () -> Unit
	val onAddTag: (name: String) -> Unit
}

val stubCreateTagCallbacks = object : CreateTagCallbacks {
	//override val onShowCreate: () -> Unit = {}
	override val onAddTag: (name: String) -> Unit = {}
}
