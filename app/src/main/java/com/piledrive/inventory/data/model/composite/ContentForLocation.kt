package com.piledrive.inventory.data.model.composite

data class ContentForLocation(
	val locationsScopedContent: Map<String, List<StashForItem>> = mapOf()
) {
	val flatContent: List<StashForItem>
		get() {
			val consolidatedMap = mutableMapOf<String, StashForItem>()
			locationsScopedContent.values.forEach { items ->
				items.forEach { item ->
					val oldStock = consolidatedMap[item.item.id]
					if(oldStock != null) {
						consolidatedMap[item.item.id] = oldStock.copy(stash = oldStock.stash.copy(amount = oldStock.stash.amount + item.stash.amount))
					} else {
						consolidatedMap[item.item.id] = item
					}
				}
			}
			return consolidatedMap.values.toList()
		}
}
