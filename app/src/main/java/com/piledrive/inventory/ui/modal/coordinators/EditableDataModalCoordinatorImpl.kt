package com.piledrive.inventory.ui.modal.coordinators

import androidx.compose.runtime.State
import com.piledrive.inventory.data.model.abstracts.FullDataModel
import com.piledrive.inventory.data.model.abstracts.SlugDataModel

interface CreateDataModalCoordinatorImpl<U : SlugDataModel> {
	val onCreateDataModel: (dataSlug: U) -> Unit
}

interface EditableDataModalCoordinatorImpl<T : FullDataModel, U : SlugDataModel> : CreateDataModalCoordinatorImpl<U> {
	val activeEditDataState: State<T?>
	val onUpdateDataModel: (updatedData: T) -> Unit
	fun showSheetWithData(data: T)
}