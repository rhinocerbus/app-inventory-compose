package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import com.piledrive.inventory.model.Location
import com.piledrive.inventory.repo.SampleRepo
import com.piledrive.inventory.ui.state.LocationContentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
	private val repo: SampleRepo
) : ViewModel() {

	private var activeLocation: Location? = null

	private val _locationContentState = MutableStateFlow<LocationContentState>(LocationContentState())
	val locationContentState: StateFlow<LocationContentState> = _locationContentState

	init {

	}

	suspend fun reloadContent() {

	}

	suspend fun addNewLocation(name: String) {
		val newLocation = Location(name)
		val updated = _locationContentState.value.copy(data = _locationContentState.value.data + newLocation)
		_locationContentState.value = updated
	}

}
