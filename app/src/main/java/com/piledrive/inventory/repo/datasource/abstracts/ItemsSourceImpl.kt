package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.ItemSlug
import kotlinx.coroutines.flow.Flow

interface ItemsSourceImpl {
	fun watchItems(): Flow<List<Item>>
	suspend fun addItem(item: ItemSlug)
}