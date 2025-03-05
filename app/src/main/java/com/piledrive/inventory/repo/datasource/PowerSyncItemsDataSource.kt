package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.repo.datasource.abstracts.ItemsSourceImpl
import com.piledrive.inventory.ui.util.UUIDv5
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class PowerSyncItemsDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : ItemsSourceImpl {


	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
	}

	override fun watchItems(): Flow<List<Item>> {
		return powerSync.db.watch(
			"SELECT * FROM items", mapper = { cursor ->
				Item(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					name = cursor.getString("name"),
					// figure out joins
					tags = listOf(),
					unit = QuantityUnit.defaultUnitBags
				)
			}
		)
	}

	override suspend fun addItem(item: ItemSlug) {
		val itemId = UUIDv5.nameUUIDFromString(item.name)
		val values = ContentValues().apply {
			put("id", itemId.toString())
			put("name", item.name)
			put("unit_id", item.unit.id)
		}
		powerSync.insert("items", values, Item::class)
		//items2tags
	}
}