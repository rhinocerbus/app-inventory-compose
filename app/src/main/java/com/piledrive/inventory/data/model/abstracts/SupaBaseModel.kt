package com.piledrive.inventory.data.model.abstracts

import com.squareup.moshi.Json

interface SupaBaseModel {
	val id: String
	@Json(name = "created_at")
	val createdAt: String
}