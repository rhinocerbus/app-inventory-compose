package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
@JsonClass(generateAdapter = true)
interface TagImpl {
	val name: String
}

data class TagSlug(
	override val name: String
): TagImpl

data class Tag(
	override val id: String = "",
	override val createdAt: String,
	override val name: String
) : TagImpl, SupaBaseModel
