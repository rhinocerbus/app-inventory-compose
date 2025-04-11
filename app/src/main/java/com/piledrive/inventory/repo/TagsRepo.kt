package com.piledrive.inventory.repo

import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.repo.datasource.PowerSyncTagsDataSource
import com.piledrive.inventory.repo.datasource.abstracts.TagsSourceImpl
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class TagsRepo @Inject constructor(
	private val powerSyncSource: PowerSyncTagsDataSource,
) : TagsSourceImpl {

	suspend fun initialize(): Flow<Int> {
		return powerSyncSource.initPowerSync()
	}

	override fun watchTags(): Flow<List<Tag>> {
		return powerSyncSource.watchTags()
	}

	override suspend fun addTag(slug: TagSlug) {
		powerSyncSource.addTag(slug)
	}

	override suspend fun updateTag(tag: Tag) {
		powerSyncSource.updateTag(tag)
	}
}