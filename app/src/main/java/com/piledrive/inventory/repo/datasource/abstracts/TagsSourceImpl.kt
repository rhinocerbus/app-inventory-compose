package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagsSourceImpl {
	fun watchTags(): Flow<List<Tag>>
	suspend fun addTag(name: String)
}