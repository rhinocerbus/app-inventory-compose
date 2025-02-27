package com.piledrive.inventory.ui.db

import android.util.Log
import com.piledrive.inventory.model.Location
import com.piledrive.inventory.model.LocationSlug
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupaBaseWrapper @Inject constructor(
	private val client: SupabaseClient
) {
	companion object {
		const val TABLE_LOCATIONS = "locations"
	}

	suspend fun getLocations(): List<Location> {
		val result = client.from(TABLE_LOCATIONS).select()
		return result.decodeList<Location>()
	}

	suspend fun addLocation(name: String) {
		val location = LocationSlug(name = name)
		val result = client.from(TABLE_LOCATIONS).insert(LocationSlug)
		Log.d("sadf", "${result.data}")
	}
}