package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.StashSlug
import com.piledrive.inventory.repo.datasource.abstracts.ItemStashesSourceImpl
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.getDouble
import com.powersync.db.getString
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PowerSyncItemStashesDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : ItemStashesSourceImpl {

	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
	}

	override fun watchItemStashes(): Flow<List<Stash>> {
		return powerSync.db.watch(
			"SELECT * FROM stashes", mapper = { cursor ->
				Stash(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					itemId = cursor.getString("item_id"),
					locationId = cursor.getString("location_id"),
					amount = cursor.getDouble("amount")
				)
			}
		)
	}

	override suspend fun addItemStash(slug: StashSlug) {
		val values = ContentValues().apply {
			put("item_id", slug.itemId)
			put("location_id", slug.locationId)
			put("amount", slug.amount)
		}
		powerSync.insert("stashes", values, Stash::class)
	}

	override suspend fun updateItemStashQuantity(stashId: String, quantity: Double) {
		val values = ContentValues().apply {
			put("amount", quantity)
		}
		powerSync.update(
			table = "stashes",
			values = values,
			whereValue = stashId,
			clazz = Stash::class
		)
	}
}