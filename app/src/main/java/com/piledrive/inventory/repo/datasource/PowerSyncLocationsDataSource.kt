package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.model.Location
import com.piledrive.inventory.repo.datasource.abstracts.LocationsSourceImpl
import com.powersync.PowerSyncDatabase
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@ViewModelScoped
class PowerSyncLocationsDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : LocationsSourceImpl {


	fun initPowerSync(): Flow<Int> {
		return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}
	}

	override fun watchLocations(): Flow<List<Location>> {
		return powerSync.db.watch(
			"SELECT * FROM locations", mapper = { cursor ->
				Location(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					name = cursor.getString("name"),
				)
			}
		)
	}

	override suspend fun addLocation(name: String) {
		val values = ContentValues().apply {
			put("name", name)
		}
		powerSync.insert("locations", values)
	}
}