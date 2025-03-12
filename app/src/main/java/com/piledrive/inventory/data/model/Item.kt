package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
interface ItemImpl {
	val name: String
	val tags: List<String>
	val unitId: String
}

//@Serializable
data class ItemSlug(
	override val name: String, override val tags: List<String>, override val unitId: String,
) : ItemImpl

//@Serializable
@JsonClass(generateAdapter = true)
data class Item(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String,
	override val name: String,
	override val tags: List<String>,
	override val unitId: String,
) : ItemImpl, SupaBaseModel
