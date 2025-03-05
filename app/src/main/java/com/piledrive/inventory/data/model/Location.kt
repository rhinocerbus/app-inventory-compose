package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

val STATIC_ID_LOCATION_ALL = "04eeb1d0-bd83-42e7-909b-b2436bded332"

//@Serializable
interface LocationImpl {
	val name: String
}

data class LocationSlug(
	override val name: String,
) : LocationImpl

@JsonClass(generateAdapter = true)
data class Location(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String,
	override val name: String,
): LocationImpl, SupaBaseModel
