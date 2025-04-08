package com.piledrive.inventory.ui.modal.coordinators

import androidx.compose.runtime.State

interface EditableDataModalCoordinatorImpl<T> {
	val activeEditDataState: State<T?>
	fun showSheetWithData(data: T)
}