package com.piledrive.inventory.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
@JsonClass(generateAdapter = true)
data class Location(
	val id: String = "",
	val name: String,
	@Json(name = "created_at")
	val createdAt: String
)

//@Serializable
@JsonClass(generateAdapter = true)
data class LocationSlug(
	val id: String? = null,
	val name: String,
	@Json(name = "created_at")
	val createdAt: String? = null
)
