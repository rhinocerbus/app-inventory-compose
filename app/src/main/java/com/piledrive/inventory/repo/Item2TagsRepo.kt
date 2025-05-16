package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.Item2TagSlug
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.repo.datasource.PowerSyncItem2TagsDataSource
import com.piledrive.inventory.repo.datasource.PowerSyncLocationsDataSource
import com.piledrive.inventory.repo.datasource.PowerSyncTagsDataSource
import com.piledrive.inventory.repo.datasource.SupaBaseLocationsDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Item2TagsRepo @Inject constructor(
	private val powerSyncSource: PowerSyncItem2TagsDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	suspend fun addItem2Tag(slug: Item2TagSlug) {
		powerSyncSource.addItem2Tag(slug)
		//supaBase.addLocation(name)
	}

	fun watchItem2Tags(): Flow<List<Item2Tag>> {
		return powerSyncSource.watchItem2Tags()
		//return supaBase.watchLocations()
	}
}