package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

val STATIC_ID_TAG_ALL = "48ac0930-8ae4-41ec-a15c-5acd47c5c2dd"

//@Serializable
interface TagImpl {
	val name: String
}

data class TagSlug(
	override val name: String
): TagImpl

@JsonClass(generateAdapter = true)
data class Tag(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String,
	override val name: String
) : TagImpl, SupaBaseModel
