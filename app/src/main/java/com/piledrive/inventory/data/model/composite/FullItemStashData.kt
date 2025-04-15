package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Stash

data class FullItemStashData(val item: FullItemData, val stash: Stash, val location: Location)
