package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.StashSlug
import com.piledrive.inventory.repo.datasource.PowerSyncItemStashesDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ItemStashesRepo @Inject constructor(
	private val powerSyncSource: PowerSyncItemStashesDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	suspend fun addItemStash(slug: StashSlug) {
		powerSyncSource.addItemStash(slug)
		//supaBase.addLocation(name)
	}

	fun watchItemStashes(): Flow<List<Stash>> {
		return powerSyncSource.watchItemStashes()
		//return supaBase.watchLocations()
	}
}