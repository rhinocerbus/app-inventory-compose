package com.piledrive.inventory.data.powersync

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

val AppSchema: Schema = Schema(
	listOf(
		Table(
			name = "locations",
			columns = listOf(
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("name")
			)
		)
	)
)