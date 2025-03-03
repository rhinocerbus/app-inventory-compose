package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
@JsonClass(generateAdapter = true)
interface ItemImpl {
	val name: String
	val tags: List<Tag>
	val unit: QuantityUnit
}

//@Serializable
data class ItemSlug(
	override val name: String, override val tags: List<Tag>, override val unit: QuantityUnit,
) : ItemImpl

//@Serializable
data class Item(
	override val id: String = "",
	override val createdAt: String,
	override val name: String,
	override val tags: List<Tag>,
	override val unit: QuantityUnit,
) : ItemImpl, SupaBaseModel
