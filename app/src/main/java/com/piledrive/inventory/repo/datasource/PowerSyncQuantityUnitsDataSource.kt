package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.model.QuantityType
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.repo.datasource.abstracts.QuantityUnitsSourceImpl
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PowerSyncQuantityUnitsDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : QuantityUnitsSourceImpl {


	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
	}

	override fun watchQuantityUnits(): Flow<List<QuantityUnit>> {
		return powerSync.db.watch(
			"SELECT * FROM units", mapper = { cursor ->
				QuantityUnit(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					name = cursor.getString("name"),
					label = cursor.getString("label"),
					type = QuantityType.valueOf(cursor.getString("type"))
				)
			}
		)
	}

	override suspend fun addQuantityUnit(slug: QuantityUnitSlug) {
		val values = ContentValues().apply {
			put("name", slug.name)
			put("label", slug.label)
			put("type", slug.type.name)
		}
		powerSync.insert("units", values, QuantityUnit::class)
	}

	override suspend fun updateQuantityUnit(unit: QuantityUnit) {
		val values = ContentValues().apply {
			put("name", unit.name)
			put("label", unit.label)
			put("type", unit.type.name)
		}
		powerSync.update("units", values, whereValue = unit.id, clazz = QuantityUnit::class)
	}
}