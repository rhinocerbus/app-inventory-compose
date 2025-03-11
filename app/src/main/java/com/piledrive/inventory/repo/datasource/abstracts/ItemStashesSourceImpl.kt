package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.StashSlug
import kotlinx.coroutines.flow.Flow

interface ItemStashesSourceImpl {
	fun watchItemStashes(): Flow<List<Stash>>
	suspend fun addItemStash(slug: StashSlug)
	suspend fun updateItemStashQuantity(stashId: String, quantity: Double)
}