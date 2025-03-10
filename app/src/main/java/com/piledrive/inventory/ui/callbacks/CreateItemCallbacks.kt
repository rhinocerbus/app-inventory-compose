package com.piledrive.inventory.ui.callbacks

interface CreateItemCallbacks {
	val onShowCreate: () -> Unit
	val onAddLocation: (name: String) -> Unit
}

val stubCreateItemCallbacks = object : CreateItemCallbacks {
	override val onShowCreate: () -> Unit = {}
	override val onAddLocation: (name: String) -> Unit = {}
}
