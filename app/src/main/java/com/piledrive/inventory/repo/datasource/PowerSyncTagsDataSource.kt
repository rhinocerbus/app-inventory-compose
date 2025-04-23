package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.repo.datasource.abstracts.TagsSourceImpl
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PowerSyncTagsDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : TagsSourceImpl {

	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
	}

	override fun watchTags(): Flow<List<Tag>> {
		return powerSync.db.watch(
			"SELECT * FROM tags", mapper = { cursor ->
				Tag(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					name = cursor.getString("name"),
				)
			}
		)
	}

	override suspend fun addTag(slug: TagSlug) {
		val values = ContentValues().apply {
			put("name", slug.name)
		}
		powerSync.insert("tags", values, Tag::class)
	}

	override suspend fun updateTag(tag: Tag) {
		val values = ContentValues().apply {
			put("name", tag.name)
		}
		powerSync.update("tags", values, whereValue = tag.id, clazz = Tag::class)
	}
}