package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.FullDataModel
import com.piledrive.inventory.data.model.abstracts.SlugDataModel
import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

val STATIC_ID_LOCATION_ALL = "04eeb1d0-bd83-42e7-909b-b2436bded332"
val STATIC_ID_NEW_FROM_TRANSFER = "683b25d0-6c62-455f-a29b-a05ff6396daa"

//@Serializable
interface LocationImpl {
	val name: String
}

data class LocationSlug(
	override val name: String,
) : LocationImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Location(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String,
	override val name: String,
): LocationImpl, FullDataModel, SupaBaseModel
