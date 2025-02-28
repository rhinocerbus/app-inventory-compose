package com.piledrive.inventory.repo

import com.piledrive.inventory.model.Location
import com.piledrive.inventory.ui.db.SupaBaseWrapper
import com.piledrive.inventory.ui.state.LocationOptions
import com.powersync.PowerSyncDatabase
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class PowerSyncRepo @Inject constructor(
	private val powerSync: PowerSyncDatabase,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	fun start(): Flow<Int> {
		return callbackFlow {
			send(0)
			powerSync.waitForFirstSync()
			send(1)
			close()
		}
	}

	fun loadLocations(): Flow<List<Location>> {
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

}