package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.repo.datasource.PowerSyncLocationsDataSource
import com.piledrive.inventory.repo.datasource.SupaBaseLocationsDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class LocationsRepo @Inject constructor(
	private val supaBase: SupaBaseLocationsDataSource,
	private val powerSyncSource: PowerSyncLocationsDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	suspend fun addLocation(name: String) {
		powerSyncSource.addLocation(name)
		//supaBase.addLocation(name)
	}

	fun watchLocations(): Flow<List<Location>> {
		return powerSyncSource.watchLocations()
		//return supaBase.watchLocations()
	}
}