package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.repo.datasource.PowerSyncTagsDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class TagsRepo @Inject constructor(
	private val powerSyncSource: PowerSyncTagsDataSource,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	suspend fun addTag(slug: TagSlug) {
		powerSyncSource.addTag(slug)
		//supaBase.addLocation(name)
	}

	fun watchTags(): Flow<List<Tag>> {
		return powerSyncSource.watchTags()
		//return supaBase.watchLocations()
	}
}