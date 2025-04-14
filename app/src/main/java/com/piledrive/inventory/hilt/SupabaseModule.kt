package com.piledrive.inventory.hilt

import com.piledrive.inventory.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.MoshiSerializer
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupaBaseModule {

	private val moshi by lazy {
		Moshi.Builder()
			.add(KotlinJsonAdapterFactory())
			.build()
	}

	@Provides
	@Singleton
	fun provideSupaBase(): SupabaseClient {

		val supabase = createSupabaseClient(
			supabaseUrl = BuildConfig.SUPABASE_URL,
			supabaseKey = BuildConfig.SUPABASE_ANON_KEY
		) {
			//aso supports moshi, jackson
			//defaultSerializer = KotlinXSerializer()
			defaultSerializer = MoshiSerializer(moshi)
			install(Auth)
			install(Postgrest)
			install(Realtime)
		}
		runBlocking {
			supabase.auth.signInAnonymously()
		}
		return supabase
	}

	@Provides
	@Singleton
	fun provideSupabaseDatabase(client: SupabaseClient): Postgrest {
		return client.postgrest
	}
}