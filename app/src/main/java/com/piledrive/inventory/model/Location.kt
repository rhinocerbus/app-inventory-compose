package com.piledrive.inventory.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(val id: String = "", val name: String, val createdAt: String)

@Serializable
data class LocationSlug(val id: String? = null, val name: String, val createdAt: String? = null)
