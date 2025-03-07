package com.piledrive.inventory.data.model.composite

data class ContentForLocation(val locationsScopedContent: Map<String, List<StockWithItem>> = mapOf())
