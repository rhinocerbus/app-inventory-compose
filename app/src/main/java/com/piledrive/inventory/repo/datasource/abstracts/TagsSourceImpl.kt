package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.TagSlug
import kotlinx.coroutines.flow.Flow

interface TagsSourceImpl {
	fun watchTags(): Flow<List<Tag>>
	suspend fun addTag(slug: TagSlug)
	suspend fun updateTag(tag: Tag)
}