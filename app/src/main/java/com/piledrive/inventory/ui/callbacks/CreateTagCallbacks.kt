package com.piledrive.inventory.ui.callbacks

interface CreateTagCallbacks {
	val onShowCreate: () -> Unit
	val onAddLocation: (name: String) -> Unit
}

val stubCreateTagCallbacks = object : CreateTagCallbacks {
	override val onShowCreate: () -> Unit = {}
	override val onAddLocation: (name: String) -> Unit = {}
}
