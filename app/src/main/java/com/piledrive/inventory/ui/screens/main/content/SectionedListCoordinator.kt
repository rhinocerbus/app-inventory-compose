package com.piledrive.inventory.ui.screens.main.content

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

interface SectionedListCoordinatorImpl {
	val expandedSectionsState: State<List<String>>
	fun toggleSectionExpansion(sectionId: String)
	fun clear()
}

open class SectionedListCoordinator(
	initialValues: List<String> = listOf()
) : SectionedListCoordinatorImpl {
	private val _expandedSectionsState: MutableState<List<String>> = mutableStateOf(initialValues)
	override val expandedSectionsState: State<List<String>> = _expandedSectionsState

	override fun toggleSectionExpansion(sectionId: String) {
		val updated = _expandedSectionsState.value.toMutableList()
		if (!updated.remove(sectionId)) {
			updated.add(sectionId)
		}
		_expandedSectionsState.value = updated
	}

	override fun clear() {
		val updated = _expandedSectionsState.value.toMutableList()
		updated.clear()
		_expandedSectionsState.value = updated
	}
}