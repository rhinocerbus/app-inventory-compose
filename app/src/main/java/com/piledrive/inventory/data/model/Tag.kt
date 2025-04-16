package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.FullDataModel
import com.piledrive.inventory.data.model.abstracts.SlugDataModel
import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

val STATIC_ID_TAG_ALL = "48ac0930-8ae4-41ec-a15c-5acd47c5c2dd"

//@Serializable
interface TagImpl {
	val name: String
	val showEmpty: Boolean
}

data class TagSlug(
	override val name: String,
	override val showEmpty: Boolean
) : TagImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Tag(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String,
	override val name: String,
	@Deprecated(
		message = "Do not use directly, only necessary because PowerSync/SQL doesn't support booleans",
		replaceWith = ReplaceWith("showEmpty"),
		level = DeprecationLevel.WARNING
	)
	@Json(name = "show_empty")
	private val showEmptyRaw: Int = 0
) : TagImpl, FullDataModel, SupaBaseModel {
	override val showEmpty: Boolean
		get() = showEmptyRaw == 1
}
