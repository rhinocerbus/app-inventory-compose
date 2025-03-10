package com.piledrive.inventory.ui.callbacks

import com.piledrive.inventory.data.model.LocationSlug

interface CreateLocationCallbacks {
	//val onShowCreate: () -> Unit
	val onAddLocation: (slug: LocationSlug) -> Unit
}

val stubCreateLocationCallbacks = object : CreateLocationCallbacks {
	//override val onShowCreate: () -> Unit = {}
	override val onAddLocation: (slug: LocationSlug) -> Unit = {}
}
