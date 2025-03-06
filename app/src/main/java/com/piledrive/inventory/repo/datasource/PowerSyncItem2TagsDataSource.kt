package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.Item2TagSlug
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.repo.datasource.abstracts.Item2TagsSourceImpl
import com.piledrive.inventory.repo.datasource.abstracts.LocationsSourceImpl
import com.piledrive.inventory.repo.datasource.abstracts.TagsSourceImpl
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@ViewModelScoped
class PowerSyncItem2TagsDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : Item2TagsSourceImpl {

	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
	}

	override fun watchItem2Tags(): Flow<List<Item2Tag>> {
		return powerSync.db.watch(
			"SELECT * FROM item_tags", mapper = { cursor ->
				Item2Tag(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					itemId = cursor.getString("item_id"),
					tagId = cursor.getString("tag_id")
				)
			}
		)
	}

	override suspend fun addItem2Tag(slug: Item2TagSlug) {
		val values = ContentValues().apply {
			put("item_id", slug.itemId)
			put("tag_id", slug.tagId)
		}
		powerSync.insert("item_tags", values, Item2Tag::class)
	}
}