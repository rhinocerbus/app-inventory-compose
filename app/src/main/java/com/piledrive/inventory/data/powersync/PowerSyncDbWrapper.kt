package com.piledrive.inventory.data.powersync

import android.content.ContentValues
import com.piledrive.inventory.data.model.abstracts.SupaBaseModel
import com.powersync.PowerSyncDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

/**
 * https://docs.powersync.com/intro/powersync-overview#getting-started
 * https://docs.powersync.com/installation/authentication-setup/development-tokens
 * https://docs.powersync.com/installation/database-connection
 * https://github.com/powersync-ja/powersync-kotlin/blob/738fd0a041c43e1efc0374fbf6357ddc504c71ed/connectors/supabase/src/commonMain/kotlin/com/powersync/connector/supabase/SupabaseConnector.kt#L160
 * https://docs.powersync.com/integration-guides/supabase-+-powersync/realtime-streaming
 * https://docs.powersync.com/client-sdk-references/kotlin-multiplatform/usage-examples
 */
class PowerSyncDbWrapper(val db: PowerSyncDatabase) {

	private val _initState = MutableStateFlow<Int>(0)
	val initState: StateFlow<Int> = _initState

	init {
		CoroutineScope(Dispatchers.Default).launch {
			/*initPowerSync().collect {

			}
			*/
			_initState.value = 0
			db.waitForFirstSync()
			_initState.value = 1
		}
	}

	//val sharedFlow = MutableSharedFlow<Int>(replay = 1)


	/*
	fun initPowerSync(): Flow<Int> {
		return callbackFlow {
			send(0)
			db.waitForFirstSync()
			send(1)
			close()
		}
	}
*/

	suspend fun insert(table: String, values: ContentValues, clazz: KClass<out SupaBaseModel>) {
		Timber.d("> performing INSERT into $table")

		val colNames = when (values.size()) {
			0 -> {
				throw (IllegalArgumentException())
			}

			// singles still require ()
			//1 -> "(${values.valueSet().first().key})"
			else -> {
				// since it's raw sql, powersync doesn't jive with supabase db defaults, need to set them up here
				// todo: add a class ref & data model interface to force presence of standard/required fields (id, created_at, updated_at if added)
				var nameConcat = "(id, created_at, "
				values.valueSet().forEachIndexed { index, mutableEntry ->
					val valuestr = "${mutableEntry.key}${if (index < values.size() - 1) ", " else ""}"
					Timber.d(valuestr)
					nameConcat += valuestr
				}
				nameConcat += ")"
				nameConcat
			}
		}

		val placeholders = when (values.size()) {
			0 -> {
				throw (IllegalArgumentException())
			}

			// singles still require ()
			//1 -> "(?)"
			else -> {
				var placeholderConcat = "(${
					if (values.containsKey("id")) {
						"'${values.get("id")}'"
					} else {
						"uuid()"
					}
				}, '${ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT)}', "
				values.valueSet().forEachIndexed { index, mutableEntry ->
					val s = "?${if (index < values.size() - 1) ", " else ""}"
					placeholderConcat += s
				}
				placeholderConcat += ")"
				placeholderConcat
			}
		}


		val cmd = "INSERT INTO $table $colNames VALUES $placeholders"
		Timber.d(cmd)
		val things = values.valueSet().map { it.value }
		val result = db.execute(cmd, things)
		Timber.d("<< result: $result")
	}

	suspend fun update(
		table: String,
		values: ContentValues,
		whereClause: String = "WHERE id = ?",
		whereValue: String,
		clazz: KClass<out SupaBaseModel>
	) {
		Timber.d("> performing UPDATE into $table")

		var colNamesAndPlaceholders = ""
		values.valueSet().forEachIndexed { index, mutableEntry ->
			colNamesAndPlaceholders += "${if (index > 0) ", " else ""}${mutableEntry.key} = ?"
		}

		val cmd = "UPDATE $table SET $colNamesAndPlaceholders $whereClause"
		Timber.d(cmd)
		val things = values.valueSet().map { it.value } + whereValue
		val result = db.execute(cmd, things)
		Timber.d("<< result: $result")
	}

	suspend fun delete(
		table: String,
		whereClause: String = "WHERE id = ?",
		whereValue: String,
		clazz: KClass<out SupaBaseModel>
	) {
		Timber.d("> performing DELETE from $table")

		val cmd = "DELETE FROM $table $whereClause"
		Timber.d(cmd)
		val things = listOf(whereValue)
		val result = db.execute(cmd, things)
		Timber.d("<< result: $result")
	}
}