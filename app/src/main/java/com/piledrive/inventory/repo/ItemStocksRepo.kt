package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Stock
import com.piledrive.inventory.data.model.StockSlug
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.repo.datasource.PowerSyncItemStocksDataSource
import com.piledrive.inventory.repo.datasource.PowerSyncLocationsDataSource
import com.piledrive.inventory.repo.datasource.PowerSyncTagsDataSource
import com.piledrive.inventory.repo.datasource.SupaBaseLocationsDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ItemStocksRepo @Inject constructor(
	private val powerSyncSource: PowerSyncItemStocksDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	suspend fun addItemStock(slug: StockSlug) {
		powerSyncSource.addItemStock(slug)
		//supaBase.addLocation(name)
	}

	fun watchItemStocks(): Flow<List<Stock>> {
		return powerSyncSource.watchItemStocks()
		//return supaBase.watchLocations()
	}
}