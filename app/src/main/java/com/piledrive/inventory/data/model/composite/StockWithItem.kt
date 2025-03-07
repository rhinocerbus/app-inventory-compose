package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Stock
import com.piledrive.inventory.data.model.Tag

data class StockWithItem(val stock: Stock, val item: Item, val tags: List<Tag>)
