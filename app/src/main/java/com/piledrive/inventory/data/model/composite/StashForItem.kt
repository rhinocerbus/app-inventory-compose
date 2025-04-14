package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag

data class StashForItem(val stash: Stash, val item: ItemWithUnit, val tags: List<Tag>)
