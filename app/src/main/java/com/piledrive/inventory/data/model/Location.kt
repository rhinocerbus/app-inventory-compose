package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
@JsonClass(generateAdapter = true)
interface LocationImpl {
	val name: String
}

data class LocationSlug(
	override val name: String,
) : LocationImpl

data class Location(
	override val id: String = "",
	override val createdAt: String,
	override val name: String,
): LocationImpl, SupaBaseModel
