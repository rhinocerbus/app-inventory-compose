package com.piledrive.inventory.ui.screens.coordinators

import com.piledrive.inventory.data.model.abstracts.FullDataModel

interface ManageDataScreenImpl<T : FullDataModel> {
	val onLaunchDataModelCreation: () -> Unit
	val onDataModelSelected: (data: T) -> Unit
}