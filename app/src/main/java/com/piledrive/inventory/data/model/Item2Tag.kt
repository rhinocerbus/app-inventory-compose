package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.FullDataModel
import com.piledrive.inventory.data.model.abstracts.SlugDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


//@Serializable
interface Item2TagImpl {
	@Json(name = "item_id")
	val itemId: String
	@Json(name = "tag_id")
	val tagId: String
}

data class Item2TagSlug(
	override val itemId: String,
	override val tagId: String
): Item2TagImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Item2Tag(
	override val id: String,
	@Json(name = "created_at")
	override val createdAt: String,
	override val itemId: String,
	override val tagId: String,
) : Item2TagImpl, FullDataModel, SupaBaseModel
