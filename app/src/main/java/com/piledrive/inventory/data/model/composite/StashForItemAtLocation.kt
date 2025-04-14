package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.Stash

data class StashForItemAtLocation(val stash: Stash, val location: Location, val item: ItemWithUnit)
