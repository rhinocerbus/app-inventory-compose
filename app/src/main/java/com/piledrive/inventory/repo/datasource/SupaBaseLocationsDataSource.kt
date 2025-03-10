package com.piledrive.inventory.repo.datasource

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.repo.datasource.abstracts.LocationsSourceImpl
import com.piledrive.inventory.ui.db.SupaBaseWrapper
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class SupaBaseLocationsDataSource @Inject constructor(
	private val supaBase: SupaBaseWrapper,
) : LocationsSourceImpl {

	override fun watchLocations(): Flow<List<Location>> {
		return callbackFlow {
			// if you want initial load
			val initialLocations = supaBase.getLocations()
			trySend(initialLocations)

			supaBase.buildLocationsChannel().collectLatest {
				// sync data here or upstream, requery or just use in-memory downstream
				Timber.d("$it.")
				when {
				}
			}
		}.flowOn(Dispatchers.Default)
	}

	override suspend fun addLocation(slug: LocationSlug) {
		withContext(Dispatchers.Default) {
			supaBase.addLocation(slug.name)
		}
	}
}