package com.piledrive.inventory.ui.callbacks

import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.data.model.TagSlug

interface CreateTagCallbacks {
	//val onShowCreate: () -> Unit
	val onAddTag: (slug: TagSlug) -> Unit
}

val stubCreateTagCallbacks = object : CreateTagCallbacks {
	//override val onShowCreate: () -> Unit = {}
	override val onAddTag: (slug: TagSlug) -> Unit = {}
}
