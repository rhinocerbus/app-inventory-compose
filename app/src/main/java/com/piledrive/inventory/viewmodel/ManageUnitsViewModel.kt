package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.QuantityUnitSlug
import com.piledrive.inventory.repo.QuantityUnitsRepo
import com.piledrive.inventory.ui.modal.create_unit.CreateQuantityUnitSheetCoordinator
import com.piledrive.inventory.ui.screens.units.content.ManageUnitsContentCoordinator
import com.piledrive.inventory.ui.state.QuantityUnitContentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageUnitsViewModel @Inject constructor(
	private val unitsRepo: QuantityUnitsRepo,
) : ViewModel() {

	init {
		//reloadContent()
	}

	fun reloadContent() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				unitsRepo.initialize().collect {
					Timber.d("units repo init status: $it")
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
							watchQuantityUnits()
						}
					}
				}
			}
		}
	}


	//  region Units data
	/////////////////////////////////////////////////

	private var unitsContent: QuantityUnitContentState = QuantityUnitContentState()
	private val _unitsContentState = MutableStateFlow<QuantityUnitContentState>(unitsContent)
	val unitsContentState: StateFlow<QuantityUnitContentState> = _unitsContentState

	fun addNewQuantityUnit(slug: QuantityUnitSlug) {
		viewModelScope.launch {
			unitsRepo.addQuantityUnit(slug)
		}
	}

	private fun updateQuantityUnit(unit: QuantityUnit) {
		viewModelScope.launch {
			unitsRepo.editQuantityUnit(unit)
		}
	}

	private fun watchQuantityUnits() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				unitsRepo.watchQuantityUnits().collect {
					Timber.d("Units received: $it")
					unitsContent = unitsContent.copy(
						data = unitsContent.data.copy(allUnits = QuantityUnit.defaultSet + it)
					)
					withContext(Dispatchers.Main) {
						_unitsContentState.value = unitsContent
					}
				}
			}
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val createQuantityUnitSheetCoordinator = CreateQuantityUnitSheetCoordinator(
		unitsContentState,
		onAddQuantityUnit = {
			addNewQuantityUnit(it)
		},
		onUpdateQuantityUnit = {
			updateQuantityUnit(it)
		}
	)

	val contentCoordinator = ManageUnitsContentCoordinator(
		unitState = unitsContentState,
		onLaunchCreateUnit = { createQuantityUnitSheetCoordinator.showSheet() },
		onUnitClicked = { createQuantityUnitSheetCoordinator.showSheet() }
	)

	/////////////////////////////////////////////////
	//  endregion
}
