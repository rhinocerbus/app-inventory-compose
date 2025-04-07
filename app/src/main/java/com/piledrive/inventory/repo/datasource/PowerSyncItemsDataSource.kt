package com.piledrive.inventory.repo.datasource

import android.content.ContentValues
import com.piledrive.inventory.data.model.Item
import com.piledrive.inventory.data.model.Item2Tag
import com.piledrive.inventory.data.model.ItemSlug
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.repo.datasource.abstracts.ItemsSourceImpl
import com.piledrive.inventory.ui.util.UUIDv5
import com.powersync.db.getString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class PowerSyncItemsDataSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : ItemsSourceImpl {


	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
	}

	/* this somewhat works but the denormalized items are rough, needs a lot more attention
	override fun watchItems(): Flow<List<Item>> {
		return powerSync.db.watch(
			"SELECT items.id as i_id, items.created_at as i_created_at, items.name as i_name, items.unit_id as i_unit_id, tags.id, tags.created_at, tags.name  FROM items " +
				"LEFT OUTER JOIN item_tags ON i_id = item_tags.item_id " +
				"LEFT OUTER JOIN tags ON item_tags.tag_id = tags.id", mapper = { cursor ->
					Timber.d("${cursor.columnNames}")
				Item(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					name = cursor.getString("name"),
					// figure out joins
					tags = listOf(),
					unit = QuantityUnit.defaultUnitBags
				).apply {
					val tag = Tag(
						id = cursor.getString("id"),
						createdAt = cursor.getString("created_at"),
						name = cursor.getString("name")
					)
					fullTags = listOf()
				}
			}
		)
	}
*/

	override fun watchItems(): Flow<List<Item>> {
		return powerSync.db.watch(
			"SELECT * FROM items", mapper = { cursor ->
				Item(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					name = cursor.getString("name"),
					// figure out joins
					unitId = cursor.getString("unit_id")
				)
			}
		)
	}

	override suspend fun addItem(item: ItemSlug) {
		val itemId = UUIDv5.nameUUIDFromString(item.name)
		val values = ContentValues().apply {
			put("id", itemId.toString())
			put("name", item.name)
			put("unit_id", item.unitId)
		}
		powerSync.insert("items", values, Item::class)

		//items2tags
		item.tagIds.forEach {
			val subVales = ContentValues().apply {
				put("item_id", itemId.toString())
				put("tag_id", it)
			}
			powerSync.insert("item_tags", subVales, Item::class)
		}
	}

	override suspend fun updateItemWithTags(item: Item, tagIds: List<String>) {
		val values = ContentValues().apply {
			put("name", item.name)
			put("unit_id", item.unitId)
		}
		powerSync.update("items", values, whereValue = item.id, clazz = Item::class)

		powerSync.delete("item_tags", whereClause = "WHERE item_id = ?", whereValue = item.id, clazz = Item2Tag::class)
		tagIds.forEach {
			val subVales = ContentValues().apply {
				put("item_id", item.id)
				put("tag_id", it)
			}
			powerSync.insert("item_tags", subVales, Item::class)
		}
	}
}