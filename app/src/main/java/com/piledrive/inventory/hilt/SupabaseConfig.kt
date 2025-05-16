package com.piledrive.inventory.hilt

import com.piledrive.inventory.BuildConfig
import com.piledrive.lib_supabase_powersync.hilt.supabase.SupabaseDependencies
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

object SupabaseConfig : SupabaseDependencies {
	override val supabaseUrl: String = BuildConfig.SUPABASE_URL
	override val supabaseAnonAccountKey: String = BuildConfig.SUPABASE_ANON_KEY
}

@Module
@InstallIn(SingletonComponent::class)
object SupabaseConfigModule {

	@Provides
	fun provideSupabaseConfig(
		// Potential dependencies of this type
	): SupabaseDependencies {
		return SupabaseConfig
	}
}
