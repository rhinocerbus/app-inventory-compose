package com.piledrive.inventory.ui.callbacks

interface ModalSheetCallbacks {
	val onDismissed: () -> Unit
}

val stubModalSheetCallbacks = object : ModalSheetCallbacks {
	override val onDismissed: () -> Unit = {}
}