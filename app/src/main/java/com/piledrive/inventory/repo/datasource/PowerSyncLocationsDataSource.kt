package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.repo.datasource.abstracts.LocationsSourceImpl
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.getString
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PowerSyncLocationsDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : LocationsSourceImpl {

	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
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

	override suspend fun addLocation(slug: LocationSlug) {
		val values = ContentValues().apply {
			put("name", slug.name)
		}
		powerSync.insert("locations", values, Location::class)
	}

	override suspend fun updateLocation(location: Location) {
		val values = ContentValues().apply {
			put("name", location.name)
		}
		powerSync.update("locations", values, whereValue = location.id, clazz = Location::class)
	}
}