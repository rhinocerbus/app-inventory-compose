package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.composite.ItemWithTags
import com.piledrive.inventory.repo.datasource.PowerSyncItemsDataSource
import com.piledrive.inventory.repo.datasource.abstracts.ItemsSourceImpl
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ItemsRepo @Inject constructor(
	private val powerSyncSource: PowerSyncItemsDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
): ItemsSourceImpl {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	override suspend fun addItem(item: ItemSlug) {
		powerSyncSource.addItem(item)
		//supaBase.addLocation(name)
	}

	override fun watchItems(): Flow<List<Item>> {
		return powerSyncSource.watchItems()
		//return supaBase.watchLocations()
	}

	override suspend fun updateItemWithTags(itemData: ItemWithTags) {
		powerSyncSource.updateItemWithTags(itemData)
	}
}