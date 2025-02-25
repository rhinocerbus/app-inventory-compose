package com.piledrive.inventory.ui.callbacks

interface CreateLocationCallbacks {
	val onShowCreate: () -> Unit
}

val stubCreateLocationCallbacks = object : CreateLocationCallbacks {
	override val onShowCreate: () -> Unit = {}
}
