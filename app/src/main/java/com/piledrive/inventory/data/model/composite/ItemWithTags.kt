package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Tag
import com.piledrive.inventory.data.model.abstracts.FullDataModel

data class ItemWithTags(val item: ItemWithUnit, val tags: List<Tag>) : FullDataModel
