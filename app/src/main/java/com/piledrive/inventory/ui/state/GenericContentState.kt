package com.piledrive.inventory.ui.state

abstract class GenericContentState(
	open val data: Any? = null,
	open val isLoading: Boolean = true,
	open val hasLoaded: Boolean = false
)
