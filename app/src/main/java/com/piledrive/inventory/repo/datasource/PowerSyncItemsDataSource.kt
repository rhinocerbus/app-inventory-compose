package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.repo.datasource.abstracts.ItemsSourceImpl
import com.piledrive.inventory.repo.datasource.abstracts.LocationsSourceImpl
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

	override suspend fun addItem(name: String, tags: List<Tag>) {
		val values = ContentValues().apply {
			put("name", name)
		}
		powerSync.insert("items", values, Item::class)
		//items2tags
	}
}