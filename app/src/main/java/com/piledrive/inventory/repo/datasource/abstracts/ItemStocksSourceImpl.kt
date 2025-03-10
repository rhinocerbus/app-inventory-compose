package com.piledrive.inventory.repo.datasource.abstracts

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.Stock
import com.piledrive.inventory.data.model.StockSlug
import kotlinx.coroutines.flow.Flow

interface ItemStocksSourceImpl {
	fun watchItemStocks(): Flow<List<Stock>>
	suspend fun addItemStock(slug: StockSlug)
}