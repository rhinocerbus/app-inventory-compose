package com.piledrive.inventory.repo

import com.piledrive.inventory.model.Location
import com.piledrive.inventory.ui.db.SupaBaseWrapper
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class SampleRepo @Inject constructor(
	private val supaBase: SupaBaseWrapper,
	//private val localSource: LocalMoviesSource,
	//private val settingsSource: LocalSettingsSource
) {

	suspend fun getAllLocations(): List<Location> {
		return withContext(Dispatchers.Default) {
			supaBase.getLocations()
		}
	}

	suspend fun addLocation(name: String) {
		withContext(Dispatchers.Default) {
			supaBase.addLocation(name)
		}
	}
}