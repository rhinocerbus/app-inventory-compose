package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.FullDataModel
import com.piledrive.inventory.data.model.abstracts.SlugDataModel
import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
interface StashImpl {
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
) : StashImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Stash(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String,
	override val itemId: String,
	override val locationId: String,
	override val amount: Double,
) : StashImpl, FullDataModel, SupaBaseModel

