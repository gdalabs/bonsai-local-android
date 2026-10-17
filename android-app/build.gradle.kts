import java.security.MessageDigest

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
val emulatorBuild = providers.gradleProperty("meshmindEmulator").orNull == "true"
val bundledModel = providers.gradleProperty("bonsaiBundledModel").orNull
val bundledFormat = providers.gradleProperty("bonsaiBundledFormat").orElse("Q2_0").get()
val bundledAssets = layout.buildDirectory.dir("generated/bundled-assets")
val prepareBundledModel = if (bundledModel != null) tasks.register("prepareBundledModel") {
    require(bundledFormat in setOf("Q2_0", "PQ2_0", "PTQ1_0")) { "Unsupported bundled format" }
    val source = file(bundledModel)
    val manifest = file("../model-manager/manifests/bonsai-$bundledFormat.json")
    inputs.files(source, manifest)
    outputs.dir(bundledAssets)
    doLast {
        @Suppress("UNCHECKED_CAST")
        val metadata = groovy.json.JsonSlurper().parse(manifest) as Map<String, Any>
        require(source.length() == (metadata["size_bytes"] as Number).toLong()) { "Bundled model size mismatch" }
        val digest = MessageDigest.getInstance("SHA-256")
        source.inputStream().use { input -> val buffer = ByteArray(1024 * 1024); while (true) {
            val n = input.read(buffer); if (n < 0) break; digest.update(buffer, 0, n)
        } }
        require(digest.digest().joinToString("") { "%02x".format(it) } == metadata["sha256"]) { "Bundled model checksum mismatch" }
        val output = bundledAssets.get().asFile.apply { mkdirs() }
        source.copyTo(output.resolve("bundled-model.gguf"), overwrite = true)
        manifest.copyTo(output.resolve("bundled-model.json"), overwrite = true)
    }
} else null
android {
    namespace = "org.gdalabs.bonsailocal"
    compileSdk = 36
    ndkVersion = "28.2.13676358"
    defaultConfig {
        applicationId = "org.gdalabs.bonsailocal"
        minSdk = 28
        targetSdk = 36
        versionCode = 5
        versionName = "0.2.3-preview" + if (bundledModel != null) "-bundled" else ""
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk { abiFilters += if (emulatorBuild) "x86_64" else "arm64-v8a" }
        externalNativeBuild { cmake {
            arguments += listOf("-DANDROID_STL=c++_shared", "-DCMAKE_BUILD_TYPE=Release")
            if (emulatorBuild) arguments += "-DMESHMIND_EMULATOR=ON"
            cppFlags += "-std=c++17"
        } }
    }
    externalNativeBuild { cmake { path = file("../inference/CMakeLists.txt"); version = "3.31.6" } }
    buildFeatures { compose = true; buildConfig = true }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    sourceSets["main"].java.srcDirs("../model-manager/src/main/kotlin")
    sourceSets["main"].assets.srcDirs("../model-manager/manifests", "../docs/notices")
    if (bundledModel != null) sourceSets["main"].assets.srcDir(bundledAssets)
    androidResources { noCompress += "gguf" }
    testOptions { unitTests.isReturnDefaultValues = true }
    buildTypes {
        release { isMinifyEnabled = false; isDebuggable = false; signingConfig = signingConfigs.getByName("debug") }
        debug { if (emulatorBuild) { applicationIdSuffix = ".emulator"; versionNameSuffix = "-emulator" } }
    }
    packaging { jniLibs.useLegacyPackaging = false }
}
if (prepareBundledModel != null) tasks.matching { it.name == "preBuild" }.configureEach { dependsOn(prepareBundledModel) }
androidComponents { beforeVariants(selector().withBuildType("release")) { if (emulatorBuild) it.enable = false } }
dependencyLocking { lockAllConfigurations() }
dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20250517")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}
