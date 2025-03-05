package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Tag
import kotlinx.coroutines.flow.Flow

interface ItemsSourceImpl {
	fun watchItems(): Flow<List<Item>>
	suspend fun addItem(name: String, tags: List<Tag>)
}