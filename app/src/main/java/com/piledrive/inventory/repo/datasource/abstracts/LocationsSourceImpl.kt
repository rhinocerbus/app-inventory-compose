package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationsSourceImpl {
	fun watchLocations(): Flow<List<Location>>
	suspend fun addLocation(name: String)
}