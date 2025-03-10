package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
interface StockImpl {
	@Json(name = "item_id")
	val itemId: String

	@Json(name = "location_id")
	val locationId: String
	val amount: Double
}

data class StashSlug(
	override val itemId: String,
	override val locationId: String,
	override val amount: Double
) : StockImpl

@JsonClass(generateAdapter = true)
data class Stash(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String,
	override val itemId: String,
	override val locationId: String,
	override val amount: Double,
) : StockImpl, SupaBaseModel

