package com.piledrive.inventory.ui.db

import android.util.Log
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * https://supabase.com/docs/reference/kotlin/installing
 * https://supabase.com/docs/reference/kotlin/initializing
 *
 * https://www.reddit.com/r/Supabase/comments/12zfn6p/supabase_error_new_row_violates_rowlevel_security/
 * https://supabase.com/docs/guides/auth/managing-user-data?queryGroups=language&language=kotlin#using-triggers
 */
@Singleton
class SupaBaseWrapper @Inject constructor(
	private val client: SupabaseClient
) {
	companion object {
		const val TABLE_LOCATIONS = "locations"
	}

	private val externalFlow = MutableSharedFlow<PostgresAction>()
	private var joined = false
	suspend fun buildLocationsChannel(): Flow<PostgresAction> {
		if (joined) {
			return externalFlow
		}
		val channel = client.channel(TABLE_LOCATIONS) {

		}
		val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public")

		//Collect the flow
		changeFlow.onEach {
			when (it) {
				is PostgresAction.Delete -> println("Deleted: ${it.oldRecord}")
				is PostgresAction.Insert -> println("Inserted: ${it.record}")
				is PostgresAction.Select -> println("Selected: ${it.record}")
				is PostgresAction.Update -> println("Updated: ${it.oldRecord} with ${it.record}")
			}
		}.launchIn(CoroutineScope(Dispatchers.Default)) // launch a new coroutine to collect the flow

		channel.subscribe()
		return externalFlow
	}

	suspend fun getLocations(): List<Location> {
		val result = client.from(TABLE_LOCATIONS).select()
		return result.decodeList<Location>()
	}

	suspend fun addLocation(name: String) {
		val location = LocationSlug(name = name)
		val result = client.from(TABLE_LOCATIONS).insert(location)
		Log.d("sadf", "${result.data}")
	}
}