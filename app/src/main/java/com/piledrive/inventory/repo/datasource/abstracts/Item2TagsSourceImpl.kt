package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.Item2TagSlug
import com.piledrive.inventory.data.model.Tag
import kotlinx.coroutines.flow.Flow

interface Item2TagsSourceImpl {
	fun watchItem2Tags(): Flow<List<Item2Tag>>
	suspend fun addItem2Tag(slug: Item2TagSlug)
}