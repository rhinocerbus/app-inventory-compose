package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
@JsonClass(generateAdapter = true)
interface StockImpl {
	@Json(name = "item_id")
	val itemId: String

	@Json(name = "location_id")
	val locationId: String
	val amount: Double
}

data class StockSlug(
	override val itemId: String,
	override val locationId: String,
	override val amount: Double
) : StockImpl

data class Stock(
	override val id: String = "",
	override val createdAt: String,
	override val itemId: String,
	override val locationId: String,
	override val amount: Double,
) : StockImpl, SupaBaseModel

