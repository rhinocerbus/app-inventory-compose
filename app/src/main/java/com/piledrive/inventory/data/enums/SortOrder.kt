package com.piledrive.inventory.data.enums

import com.piledrive.inventory.R

/*
	note:
	was considering a least/most order, but that really requires unit conversions. that's possible, but would look like:
	- adding predefined units for all common weights (lbs, oz) AND volumes (p, q, g, fl-oz)
	- having a category for weight, volume
	- adding conversions down to smallest unit (oz, fl-oz)
	- deciding if oz or fl-oz is "bigger" - maybe no necessary
 */
enum class SortOrder(val iconResId: Int) {
	NAME(R.drawable.baseline_sort_by_alpha_24),
	LAST_UPDATED(R.drawable.baseline_edit_note_24),
	LAST_ADDED(R.drawable.baseline_playlist_add_24);

	companion object {
		val DEFAULT: SortOrder = NAME
	}
}