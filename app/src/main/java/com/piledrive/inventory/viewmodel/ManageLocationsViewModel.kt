package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.LocationSlug
import com.piledrive.inventory.repo.LocationsRepo
import com.piledrive.inventory.ui.modal.create_location.CreateLocationModalSheetCoordinator
import com.piledrive.inventory.ui.screens.locations.content.ManageLocationsContentCoordinator
import com.piledrive.inventory.ui.state.LocationContentState
import com.piledrive.inventory.ui.state.LocationOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageLocationsViewModel @Inject constructor(
	private val locationsRepo: LocationsRepo,
) : ViewModel() {

	init {
		//reloadContent()
	}

	fun reloadContent() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				locationsRepo.initialize().collect {
					Timber.d("tags repo init status: $it")
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

	//  region Location data
	/////////////////////////////////////////////////

	private var userLocationsContent: LocationContentState = LocationContentState()
	private val _userLocationContentFlow = MutableStateFlow(userLocationsContent)

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
						),
						hasLoaded = true,
						isLoading = false
					)
					withContext(Dispatchers.Main) {
						_userLocationContentFlow.value = userLocationsContent
					}
				}
			}
		}
	}

	private fun addNewLocation(slug: LocationSlug) {
		viewModelScope.launch {
			locationsRepo.addLocation(slug)
		}
	}

	private fun updateLocation(location: Location) {
		viewModelScope.launch {
			locationsRepo.updateLocation(location)
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val contentCoordinator = ManageLocationsContentCoordinator(
		locationsSourceFlow = _userLocationContentFlow,
		createLocationCoordinator = CreateLocationModalSheetCoordinator(
			locationsSourceFlow = _userLocationContentFlow,
			onCreateDataModel = {
				addNewLocation(it)
			},
			onUpdateDataModel = {
				updateLocation(it)
			}
		)
	)

	/////////////////////////////////////////////////
	//  endregion
}
