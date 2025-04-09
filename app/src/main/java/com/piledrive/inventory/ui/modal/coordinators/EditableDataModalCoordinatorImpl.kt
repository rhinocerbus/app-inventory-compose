package com.piledrive.inventory.ui.modal.coordinators

import androidx.compose.runtime.State
import com.piledrive.inventory.data.model.abstracts.FullDataModel
import com.piledrive.inventory.data.model.abstracts.SlugDataModel

interface EditableDataModalCoordinatorImpl<T : FullDataModel, U : SlugDataModel> {
	val activeEditDataState: State<T?>
	val onCreateDataModel: (dataSlug: U) -> Unit
	val onUpdateDataModel: (updatedData: T) -> Unit
	fun showSheetWithData(data: T)
}