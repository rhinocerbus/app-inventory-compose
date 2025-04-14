package com.piledrive.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import com.piledrive.inventory.repo.TagsRepo
import com.piledrive.inventory.ui.modal.create_tag.CreateTagSheetCoordinator
import com.piledrive.inventory.ui.screens.tags.content.ManageTagsContentCoordinator
import com.piledrive.inventory.ui.state.TagOptions
import com.piledrive.inventory.ui.state.TagsContentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageTagsViewModel @Inject constructor(
	private val tagsRepo: TagsRepo,
) : ViewModel() {

	init {
		//reloadContent()
	}

	fun reloadContent() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				tagsRepo.initialize().collect {
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
							watchTags()
						}
					}
				}
			}
		}
	}


	//  region Tags data
	/////////////////////////////////////////////////

	private var userTagsContent: TagsContentState = TagsContentState()
	private val _userTagsContentFlow = MutableStateFlow<TagsContentState>(userTagsContent)
	val userTagsContentFlow: StateFlow<TagsContentState> = _userTagsContentFlow

	private fun watchTags() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				tagsRepo.watchTags().collect {
					Timber.d("Tags received: $it")
					userTagsContent = userTagsContent.copy(
						data = TagOptions(
							userTags = it,
						),
						hasLoaded = true,
						isLoading = false
					)
					withContext(Dispatchers.Main) {
						_userTagsContentFlow.value = userTagsContent
					}
				}
			}
		}
	}

	fun addNewTag(slug: TagSlug) {
		viewModelScope.launch {
			tagsRepo.addTag(slug)
		}
	}

	fun updateTag(tag: Tag) {
		viewModelScope.launch {
			tagsRepo.updateTag(tag)
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region UI Coordinators
	/////////////////////////////////////////////////

	val contentCoordinator = ManageTagsContentCoordinator(
		tagsSourceFlow = userTagsContentFlow,
		createTagCoordinator = CreateTagSheetCoordinator(
			userTagsContentFlow,
			onCreateDataModel = {
				addNewTag(it)
			},
			onUpdateDataModel = {
				updateTag(it)
			}
		)
	)

	/////////////////////////////////////////////////
	//  endregion
}
