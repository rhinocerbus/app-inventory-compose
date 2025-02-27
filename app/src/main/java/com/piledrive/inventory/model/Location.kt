package com.piledrive.inventory.model

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

//@Serializable
@JsonClass(generateAdapter = true)
data class Location(val id: String = "", val name: String, val createdAt: String)

//@Serializable
@JsonClass(generateAdapter = true)
data class LocationSlug(val id: String? = null, val name: String, val createdAt: String? = null)
