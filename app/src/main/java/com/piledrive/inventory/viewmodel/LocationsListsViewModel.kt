package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.model.Location
import com.piledrive.inventory.model.Tag
import com.piledrive.inventory.repo.LocationsRepo
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LocationsListsViewModel @Inject constructor(
	private val locationsRepo: LocationsRepo,
) : ViewModel() {

	companion object {
		private val allTags = Tag("All")
	}

	private var activeLocation: Location? = null


	private var userLocationsContent: LocationContentState = LocationContentState()
	private val _userLocationContentState = MutableStateFlow<LocationContentState>(userLocationsContent)
	val userLocationContentState: StateFlow<LocationContentState> = _userLocationContentState

	init {
		userLocationsContent = userLocationsContent.copy(hasLoaded = true)
		_userLocationContentState.value = userLocationsContent
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				locationsRepo.initialize().collect {
					Timber.d("locations repo init status: $it")
					when (it) {
						-1 -> {
							// init error
							// todo - add error ui state
						}

						0 -> {
							// started
						}

						1 -> {
							// done
							watchLocations()
						}
					}
				}
			}
		}
	}

	fun reloadContent() {
		viewModelScope.launch {
			/*
			val apiLocations = repo.getAllLocations()
			val current = userLocationsContent.data
			userLocationsContent = userLocationsContent.copy(data = current.copy(userLocations = apiLocations))
			val updated = _userLocationContentState.value.copy()
			_userLocationContentState.value = updated
*/

		}
	}

	fun addNewLocation(name: String) {
		viewModelScope.launch {
			locationsRepo.addLocation(name)
		}
	}

	private fun watchLocations() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				locationsRepo.watchLocations().collect {
					Timber.d("Locations received: $it")
					val flatLocations = listOf(LocationOptions.defaultLocation, *it.toTypedArray())
					userLocationsContent = LocationContentState(
						data = LocationOptions(
							allLocations = flatLocations,
							userLocations = it,
							currentLocation = userLocationsContent.data.currentLocation
						)
					)
					withContext(Dispatchers.Main) {
						_userLocationContentState.value = userLocationsContent
					}
				}
			}
		}
	}

	fun changeLocation(loc: Location) {
		userLocationsContent = userLocationsContent.copy(
			data = userLocationsContent.data.copy(currentLocation = loc)
		)
		_userLocationContentState.value = userLocationsContent
	}
}
