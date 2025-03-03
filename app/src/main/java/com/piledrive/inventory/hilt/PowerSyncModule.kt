package com.piledrive.inventory.hilt

import android.content.Context
import com.piledrive.inventory.BuildConfig
import com.piledrive.inventory.data.powersync.AppSchema
import com.piledrive.inventory.data.powersync.PowerSyncDbWrapper
import com.piledrive.inventory.data.powersync.PowerSyncSupabaseConnector
import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PowerSyncModule {

	@Provides
	@Singleton
	fun providePowerSyncDatabase(@ApplicationContext appCtx: Context, supabaseClient: SupabaseClient): PowerSyncDbWrapper {
		val driverFactory = DatabaseDriverFactory(appCtx)
		val db = PowerSyncDatabase(driverFactory, AppSchema)
		val connector = PowerSyncSupabaseConnector(supabaseClient, BuildConfig.POWERSYNC_URL)
		runBlocking {
			db.connect(connector)
		}
		return PowerSyncDbWrapper(db)
	}
}