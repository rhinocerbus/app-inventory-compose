package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.piledrive.inventory.model.Location
import com.piledrive.inventory.model.Tag
import com.piledrive.inventory.repo.PowerSyncRepo
import com.piledrive.inventory.repo.SampleRepo
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
class SampleViewModel @Inject constructor(
	private val repo: SampleRepo,
	private val powerSyncRepo: PowerSyncRepo
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
		watchLocations()
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
			repo.addLocation(name)
			/*
					val newLocation = Location(name)
		val curr = userLocationsContent.data
		val updatedContent = userLocationsContent.data.copy(
			allLocations = curr.allLocations + newLocation,
			userLocations = curr.userLocations + newLocation
		)
		userLocationsContent = userLocationsContent.copy(data = updatedContent, hasLoaded = true)
		_userLocationContentState.value = userLocationsContent

			 */
		}
	}

	fun watchLocations() {
		viewModelScope.launch {
			repo.watchLocations().collect {

			}
		}

		viewModelScope.launch {
		  withContext(Dispatchers.Default) {
				powerSyncRepo.start().collect {
					Timber.d("$it")
					if(it == 1) {
						reloadContent()
					}
				}
		  }
		}

		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				powerSyncRepo.loadLocations().collect {
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
