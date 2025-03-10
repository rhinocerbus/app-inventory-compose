package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Stock
import com.piledrive.inventory.data.model.StockSlug
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.repo.datasource.abstracts.ItemStocksSourceImpl
import com.piledrive.inventory.repo.datasource.abstracts.LocationsSourceImpl
import com.piledrive.inventory.repo.datasource.abstracts.TagsSourceImpl
import com.powersync.db.getDouble
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@ViewModelScoped
class PowerSyncItemStocksDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : ItemStocksSourceImpl {

	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
	}

	override fun watchItemStocks(): Flow<List<Stock>> {
		return powerSync.db.watch(
			"SELECT * FROM stocks", mapper = { cursor ->
				Stock(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					itemId =  cursor.getString("item_id"),
					locationId = cursor.getString("location_id"),
					amount = cursor.getDouble("amount")
				)
			}
		)
	}

	override suspend fun addItemStock(slug: StockSlug) {
		val values = ContentValues().apply {
			put("item_id", slug.itemId)
			put("location_id", slug.locationId)
			put("amount", slug.amount)
		}
		powerSync.insert("stocks", values, Stock::class)
	}
}