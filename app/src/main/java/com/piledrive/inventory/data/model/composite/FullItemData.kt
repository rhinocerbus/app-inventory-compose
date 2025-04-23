package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.abstracts.FullDataModel

data class FullItemData(val item: Item, val unit: QuantityUnit, val tags: List<Tag>) : FullDataModel
