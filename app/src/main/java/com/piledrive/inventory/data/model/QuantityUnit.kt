package com.piledrive.inventory.data.model

import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@Serializable
interface QuantityUnitImpl {
	val name: String
	val label: String
	val type: QuantityType
}

open class QuantityUnitSlug(
	override val name: String, override val label: String, override val type: QuantityType
) : QuantityUnitImpl

@JsonClass(generateAdapter = true)
data class QuantityUnit(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String,
	override val name: String,
	override val label: String,
	override val type: QuantityType,
) : QuantityUnitImpl, SupaBaseModel {
	companion object {
		const val DEFAULT_ID_BAGS = "-1"
		const val DEFAULT_ID_POUNDS = "-2"

		val defaultUnitBags = QuantityUnit(DEFAULT_ID_BAGS, createdAt = "0", "bags", "bags", QuantityType.WHOLE)
		val defaultUnitPounds = QuantityUnit(
			DEFAULT_ID_POUNDS,
			createdAt = "0",
			"weight",
			"lbs",
			QuantityType.DECIMAL
		)
		//val defaultUnitVague = QuantityUnit("vauge", "lbs", QuantityType.DECIMAL)
		val defaultSet: List<QuantityUnit> = listOf(defaultUnitBags, defaultUnitPounds)
	}
}

enum class QuantityType { UNKNOWN, WHOLE, DECIMAL, /*BINARY */ }

// todo
//enum class VagueAmounts(val value: Int) { NONE(0), SOME(1), LOTS(2) }