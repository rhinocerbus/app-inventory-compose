plugins {
	// core android
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.google.ksp)
	// DI
	alias(libs.plugins.hilt.android)
	// serialization
	kotlin(libs.plugins.kotlin.serialization.get().pluginId).version(libs.versions.kotlin)
}

android {
	namespace = "com.piledrive.inventory"
	compileSdk = 35

	buildFeatures.buildConfig = true

	defaultConfig {
		applicationId = "com.piledrive.inventory"
		minSdk = 27
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


		buildConfigField("String", "SUPABASE_URL", "\"https://qchankldevimabcnapws.supabase.co\"")
		buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFjaGFua2xkZXZpbWFiY25hcHdzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA0MzA4NjAsImV4cCI6MjA1NjAwNjg2MH0.VqmE24t68BIFPO0ffNRfZmy33sj_7uAFYaEedQuK7NU\"")
		buildConfigField("String", "POWERSYNC_URL", "\"https://67c1ea67586c8d282a0671fa.powersync.journeyapps.com\"")
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	// core
	implementation(libs.androidx.core.ktx)
	// compose
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	debugImplementation(libs.androidx.compose.ui.tooling)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.compose.material3)
	// androidx/jetpack/compose
	implementation(libs.androidx.compose.activity)
	implementation(libs.androidx.compose.navigation)
	implementation(libs.androidx.compose.livedata)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.compose.ui.graphics.android)
	implementation(libs.androidx.compose.constraint)

	// internal libraries (no version necessary)
	implementation(libs.lib.compose.components)

	// DI
	implementation(libs.hilt)
	ksp(libs.hilt.compiler)
	implementation(libs.hilt.navigation)

	// supabase
	implementation(platform(libs.supabase.bom))
	implementation(libs.supabase.db)
	implementation(libs.supabase.realtime)
	implementation(libs.supabase.auth)
	implementation(libs.supabase.moshi)
	implementation(libs.ktor)

	// serialization
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.moshi.kotlin)
	ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")

	// powersync
	implementation(libs.powersync.core)
	implementation(libs.powersync.supabase)

	// logging
	implementation(libs.timber)

	// testing
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
}
