package com.piledrive.inventory.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

@Module
@InstallIn(SingletonComponent::class)
object SupaBaseModule {

	@Provides
	fun provideSupaBase(): SupabaseClient {

		val supabase = createSupabaseClient(
			supabaseUrl = "https://qchankldevimabcnapws.supabase.co",
			supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFjaGFua2xkZXZpbWFiY25hcHdzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA0MzA4NjAsImV4cCI6MjA1NjAwNjg2MH0.VqmE24t68BIFPO0ffNRfZmy33sj_7uAFYaEedQuK7NU"
		) {
			install(Postgrest)
		}
		return supabase
	}
}