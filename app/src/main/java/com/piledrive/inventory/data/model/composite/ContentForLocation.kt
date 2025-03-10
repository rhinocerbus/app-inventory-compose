package com.piledrive.inventory.data.model.composite

data class ContentForLocation(
	val locationsScopedContent: Map<String, List<StashForItem>> = mapOf()
) {
	val flatContent: List<StashForItem>
		get() {
			val consolidatedMap = mutableMapOf<String, StashForItem>()
			locationsScopedContent.values.forEach { items ->
				items.forEach { item ->
					val oldStash = consolidatedMap[item.item.id]
					if(oldStash != null) {
						consolidatedMap[item.item.id] = oldStash.copy(stash = oldStash.stash.copy(amount = oldStash.stash.amount + item.stash.amount))
					} else {
						consolidatedMap[item.item.id] = item
					}
				}
			}
			return consolidatedMap.values.toList()
		}
}
