package com.piledrive.inventory.repo.datasource

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
	private val powerSync: PowerSyncDatabase,
) : LocationsSourceImpl {


	fun initPowerSync(): Flow<Int> {
		return callbackFlow {
			send(0)
			powerSync.waitForFirstSync()
			send(1)
			close()
		}
	}

	override fun watchLocations(): Flow<List<Location>> {
		return powerSync.watch(
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
		//raw sql i think - look it up
		powerSync.execute("")
	}
}