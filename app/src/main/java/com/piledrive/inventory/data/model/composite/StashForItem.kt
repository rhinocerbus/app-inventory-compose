package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag

data class StashForItem(val stash: Stash, val item: Item, val tags: List<Tag>)
