package com.piledrive.inventory.ui.callbacks

import com.piledrive.inventory.model.Location
import com.piledrive.inventory.model.Tag

interface ContentFilterCallbacks {
	val onLocationChanged: (loc: Location) -> Unit
	val onTagChanged: (tag: Tag) -> Unit
}

val stubContentFilterCallbacks = object : ContentFilterCallbacks {
	override val onLocationChanged: (loc: Location) -> Unit = {}
	override val onTagChanged: (tag: Tag) -> Unit = {}
}
