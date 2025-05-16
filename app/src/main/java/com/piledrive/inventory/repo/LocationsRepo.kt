package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.repo.datasource.PowerSyncLocationsDataSource
import com.piledrive.inventory.repo.datasource.abstracts.LocationsSourceImpl
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationsRepo @Inject constructor(
	private val powerSyncSource: PowerSyncLocationsDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
): LocationsSourceImpl {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	override suspend fun addLocation(slug: LocationSlug) {
		powerSyncSource.addLocation(slug)
		//supaBase.addLocation(name)
	}

	override suspend fun updateLocation(location: Location) {
		powerSyncSource.updateLocation(location)
	}

	override fun watchLocations(): Flow<List<Location>> {
		return powerSyncSource.watchLocations()
		//return supaBase.watchLocations()
	}
}