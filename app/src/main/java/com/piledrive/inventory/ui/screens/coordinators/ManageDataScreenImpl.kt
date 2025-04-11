package com.piledrive.inventory.ui.screens.coordinators

import com.piledrive.inventory.data.model.abstracts.FullDataModel

interface ManageDataScreenImpl<T : FullDataModel> {
	fun launchDataModelCreation()
	fun launchDataModelEdit(data: T)
}