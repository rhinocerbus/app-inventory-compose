package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.repo.datasource.PowerSyncQuantityUnitsDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class QuantityUnitsRepo @Inject constructor(
	private val powerSyncSource: PowerSyncQuantityUnitsDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	suspend fun addQuantityUnit(slug: QuantityUnitSlug) {
		powerSyncSource.addQuantityUnit(slug)
		//supaBase.addQuantityUnit(slug)
	}

	fun watchQuantityUnits(): Flow<List<QuantityUnit>> {
		return powerSyncSource.watchQuantityUnits()
		//return supaBase.watchQuantityUnits()
	}
}