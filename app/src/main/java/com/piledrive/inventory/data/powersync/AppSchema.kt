package com.piledrive.inventory.data.powersync

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

val AppSchema: Schema = Schema(
	listOf(
		Table(
			name = "item_tags",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("item_id"),
				Column.text("tag_id"),
			)
		),
		Table(
			name = "items",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("name"),
				Column.text("unit_id"),
			)
		),
		Table(
			name = "locations",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("name")
			)
		),
		Table(
			name = "units",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("name"),
				Column.text("label"),
				Column.text("type"),
			)
		),
		Table(
			name = "stashes",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("item_id"),
				Column.text("location_id"),
				Column.real("amount"),
			)
		),
		Table(
			name = "tags",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("name"),
				Column.integer("show_empty")
			)
		),
	)
)