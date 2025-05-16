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

	id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
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

secrets {
	// To add your Maps API key to this project:
	// 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
	// 2. Add this line, where YOUR_API_KEY is your API key:
	//        MAPS_API_KEY=YOUR_API_KEY
	propertiesFileName = "secrets.properties"

	// A properties file containing default secret values. This file can be
	// checked in version control.
	defaultPropertiesFileName = "local.defaults.properties"
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
	implementation(libs.lib.supabase.powersync)

	// DI
	implementation(libs.hilt)
	ksp(libs.hilt.compiler)
	implementation(libs.hilt.navigation)

	// supabase
	implementation(platform(libs.supabase.bom)) {
		isTransitive = true
	}
	implementation(libs.supabase.db) {
		isTransitive = true
	}
	implementation(libs.supabase.realtime) {
		isTransitive = true
	}
	implementation(libs.supabase.auth) {
		isTransitive = true
	}
	implementation(libs.supabase.moshi) {
		isTransitive = true
	}
	implementation(libs.ktor) {
		isTransitive = true
	}

	// serialization
	implementation(libs.kotlinx.serialization.json){
		isTransitive = true
	}
	implementation(libs.moshi.kotlin){
		isTransitive = true
	}
	ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")

	// powersync
	implementation(libs.powersync.core){
		isTransitive = true
	}
	implementation(libs.powersync.supabase){
		isTransitive = true
	}

	// logging
	implementation(libs.timber)

	// testing
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
}
