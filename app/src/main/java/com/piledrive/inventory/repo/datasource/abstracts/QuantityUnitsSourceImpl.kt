package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import kotlinx.coroutines.flow.Flow

interface QuantityUnitsSourceImpl {
	fun watchQuantityUnits(): Flow<List<QuantityUnit>>
	suspend fun addQuantityUnit(slug: QuantityUnitSlug)
}