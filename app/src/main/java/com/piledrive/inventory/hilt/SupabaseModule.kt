package com.piledrive.inventory.hilt

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.serializer.MoshiSerializer
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
			supabaseUrl = "https://qchankldevimabcnapws.supabase.co",
			supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFjaGFua2xkZXZpbWFiY25hcHdzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA0MzA4NjAsImV4cCI6MjA1NjAwNjg2MH0.VqmE24t68BIFPO0ffNRfZmy33sj_7uAFYaEedQuK7NU"
		) {
			//aso supports moshi, jackson
			//defaultSerializer = KotlinXSerializer()
			defaultSerializer = MoshiSerializer(moshi)
			install(Postgrest)
			install(Realtime)
		}
		return supabase
	}

	@Provides
	@Singleton
	fun provideSupabaseDatabase(client: SupabaseClient): Postgrest {
		return client.postgrest
	}
}