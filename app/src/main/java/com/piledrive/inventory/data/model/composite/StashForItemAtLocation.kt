package com.piledrive.inventory.data.model.composite

import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Location
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.model.Stash
import com.piledrive.inventory.data.model.Tag

data class StashForItemAtLocation(val stash: Stash, val location: Location, val item: Item, val quantityUnit: QuantityUnit)
