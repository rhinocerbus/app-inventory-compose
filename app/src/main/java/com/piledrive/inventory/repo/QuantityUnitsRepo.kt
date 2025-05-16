package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.repo.datasource.PowerSyncQuantityUnitsDataSource
import com.piledrive.inventory.repo.datasource.abstracts.QuantityUnitsSourceImpl
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuantityUnitsRepo @Inject constructor(
	private val powerSyncSource: PowerSyncQuantityUnitsDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) : QuantityUnitsSourceImpl {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	override fun watchQuantityUnits(): Flow<List<QuantityUnit>> {
		return powerSyncSource.watchQuantityUnits()
		//return supaBase.watchQuantityUnits()
	}

	override suspend fun addQuantityUnit(slug: QuantityUnitSlug) {
		powerSyncSource.addQuantityUnit(slug)
		//supaBase.addQuantityUnit(slug)
	}

	override suspend fun updateQuantityUnit(unit: QuantityUnit) {
		powerSyncSource.updateQuantityUnit(unit)
	}
}