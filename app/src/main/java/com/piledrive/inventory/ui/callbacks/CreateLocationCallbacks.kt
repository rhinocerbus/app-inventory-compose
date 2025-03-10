package com.piledrive.inventory.ui.callbacks

interface CreateLocationCallbacks {
	//val onShowCreate: () -> Unit
	val onAddLocation: (name: String) -> Unit
}

val stubCreateLocationCallbacks = object : CreateLocationCallbacks {
	//override val onShowCreate: () -> Unit = {}
	override val onAddLocation: (name: String) -> Unit = {}
}
