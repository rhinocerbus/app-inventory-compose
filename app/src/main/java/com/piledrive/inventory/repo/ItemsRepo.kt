package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.repo.datasource.PowerSyncItemsDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ItemsRepo @Inject constructor(
	private val powerSyncSource: PowerSyncItemsDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	suspend fun addItem(name: String, tags: List<Tag>) {
		powerSyncSource.addItem(name, tags)
		//supaBase.addLocation(name)
	}

	fun watchTags(): Flow<List<Item>> {
		return powerSyncSource.watchItems()
		//return supaBase.watchLocations()
	}
}