package com.piledrive.inventory.data.model

import kotlin.Unit

//@Serializable
data class Item(val name: String, val tags: List<Tag>, val unit: Unit, val stocks: List<Stock>)
