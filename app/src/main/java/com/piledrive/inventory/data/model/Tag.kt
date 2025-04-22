package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.FullDataModel
import com.piledrive.inventory.data.model.abstracts.SlugDataModel
import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

const val STATIC_ID_TAG_ALL = "48ac0930-8ae4-41ec-a15c-5acd47c5c2dd"
const val STATIC_ID_TAG_MEAT = "f537d5e3-f3c8-41d9-b577-f3609a1d097c"
const val STATIC_ID_TAG_VEGGIES = "4f66396b-1503-40cf-b168-dba9c593d510"
const val STATIC_ID_TAG_FRUIT = "bb5c6c6d-dd66-4a41-bad5-1a5861f8f166"
const val STATIC_ID_TAG_LEFTOVERS = "722aaed3-77ea-415f-afec-e3a3149a8bf9"
const val STATIC_ID_TAG_STAPLES = "adeed3f9-0aed-4dc9-a8dd-306d10d950cd"
const val STATIC_ID_TAG_PREPARED = "d0720703-a76c-4a41-9b78-0e6b57c09d33"

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
	val predefined: Boolean = false,
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
	val showEmptyRaw: Int = 0,
) : TagImpl, FullDataModel, SupaBaseModel {
	override val showEmpty: Boolean
		get() = showEmptyRaw == 1
}

val everythingTag = Tag(predefined = true, STATIC_ID_TAG_ALL, "", "Everything")
val predefinedTagSet: List<Tag> = listOf(
	Tag(predefined = true, STATIC_ID_TAG_MEAT, createdAt = "", name = "Meat"),
	Tag(predefined = true, STATIC_ID_TAG_VEGGIES, createdAt = "", name = "Veggies"),
	Tag(predefined = true, STATIC_ID_TAG_FRUIT, createdAt = "", name = "Fruit"),
	Tag(predefined = true, STATIC_ID_TAG_LEFTOVERS, createdAt = "", name = "Leftovers"),
	Tag(predefined = true, STATIC_ID_TAG_STAPLES, createdAt = "", name = "Staples", showEmptyRaw = 1),
	Tag(predefined = true, STATIC_ID_TAG_PREPARED, createdAt = "", name = "Prepared"),
)
