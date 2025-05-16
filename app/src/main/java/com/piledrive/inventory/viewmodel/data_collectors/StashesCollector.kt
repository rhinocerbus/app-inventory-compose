@file:OptIn(ExperimentalCoroutinesApi::class)

package com.piledrive.inventory.viewmodel.data_collectors

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.composite.FullStashData
import com.piledrive.inventory.data.model.composite.FullStashesContent
import com.piledrive.inventory.ui.state.FullStashesContentState
import com.piledrive.inventory.ui.state.ItemStashContentState
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class StashesCollector(
	coroutineScope: CoroutineScope,
	locationsSourceFlow: Flow<List<Location>>,
	stashesSourceFlow: Flow<List<Stash>>
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val locationsSource = watchLocations(locationsSourceFlow)
			val stashesSource = watchItemStashes(stashesSourceFlow)

			merge(locationsSource, stashesSource)
				.debounce(500)
				.collect {
					rebuildStashesWithLocations()
				}
		}
	}

	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var locationsContent: LocationContentState = LocationContentState()
	private val _locationsContentFlow = MutableStateFlow(locationsContent)
	val locationsContentFlow: StateFlow<LocationContentState> = _locationsContentFlow

	private fun watchLocations(locationsSourceFlow: Flow<List<Location>>): Flow<Unit> {
		return locationsSourceFlow.mapLatest {
			Timber.d("Locations received: $it")
			val flatLocations = listOf(LocationOptions.defaultLocation, *it.toTypedArray())
			locationsContent = LocationContentState(
				data = LocationOptions(
					allLocations = flatLocations,
					userLocations = it,
				),
				hasLoaded = true,
				isLoading = false
			)
			withContext(Dispatchers.Main) {
				_locationsContentFlow.value = locationsContent
			}
		}
	}


	private var itemStashesContent: ItemStashContentState = ItemStashContentState()
	private val _itemStashesContentFlow = MutableStateFlow(itemStashesContent)
	val itemStashesContentFlow: StateFlow<ItemStashContentState> = _itemStashesContentFlow

	private fun watchItemStashes(stashesSourceFlow: Flow<List<Stash>>): Flow<Unit> {
		return stashesSourceFlow.mapLatest {
			Timber.d("Stashes received: $it")
			itemStashesContent = itemStashesContent.copy(
				data = itemStashesContent.data.copy(itemStashes = it)
			)
			withContext(Dispatchers.Main) {
				_itemStashesContentFlow.value = itemStashesContent
			}
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Composite data outputs
	/////////////////////////////////////////////////

	private var fullStashesContent: FullStashesContentState = FullStashesContentState()
	private val _fullStashesContentFlow = MutableStateFlow<FullStashesContentState>(fullStashesContent)
	val fullStashesContentFlow: StateFlow<FullStashesContentState> = _fullStashesContentFlow

	private suspend fun rebuildStashesWithLocations() {
		val locations = locationsContent.data.userLocations
		val stashes = itemStashesContent.data.itemStashes

		val unsortedFullStashes: List<FullStashData> = stashes.mapNotNull { stash ->
			val location = locations.firstOrNull { it.id == stash.locationId } ?: return@mapNotNull null
			FullStashData(location = location, stash = stash)
		}
		val content = FullStashesContent(
			unsortedFullStashes
		)
		val updated = fullStashesContent.copy(
			data = content
		)
		withContext(Dispatchers.Main) {
			_fullStashesContentFlow.value = updated
		}
	}

	/////////////////////////////////////////////////
	//  endregion
}