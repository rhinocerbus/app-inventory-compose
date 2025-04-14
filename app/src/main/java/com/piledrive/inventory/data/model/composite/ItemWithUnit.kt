package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.QuantityUnit

data class ItemWithUnit(val item: Item, val unit: QuantityUnit)
