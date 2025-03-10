package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Stock

data class ContentForLocation(
	val locationsScopedContent: Map<String, List<StockWithItem>> = mapOf()
) {
	val flatContent: List<StockWithItem>
		get() {
			val consolidatedMap = mutableMapOf<String, StockWithItem>()
			locationsScopedContent.values.forEach { items ->
				items.forEach { item ->
					val oldStock = consolidatedMap[item.item.id]
					if(oldStock != null) {
						consolidatedMap[item.item.id] = oldStock.copy(stock = oldStock.stock.copy(amount = oldStock.stock.amount + item.stock.amount))
					} else {
						consolidatedMap[item.item.id] = item
					}
				}
			}
			return consolidatedMap.values.toList()
		}
}
