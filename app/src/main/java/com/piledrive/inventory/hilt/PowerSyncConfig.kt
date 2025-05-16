package com.piledrive.inventory.hilt

import com.piledrive.inventory.BuildConfig
import com.piledrive.inventory.data.powersync.AppSchema
import com.piledrive.lib_supabase_powersync.hilt.powersync.PowerSyncDependencies
import com.powersync.db.schema.Schema
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

object PowerSyncConfig : PowerSyncDependencies {
	override val appSchema: Schema = AppSchema
	override val powerSyncUrl: String = BuildConfig.POWERSYNC_URL
}

@Module
@InstallIn(SingletonComponent::class)
object PowerSyncConfigModule {

	@Provides
	fun providePowerSyncConfig(
		// Potential dependencies of this type
	): PowerSyncDependencies {
		return PowerSyncConfig
	}
}
