package org.gdalabs.bonsailocal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineRuntimeTest {
    @Test fun packageHasNoInternetPermission() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val info = context.packageManager.getPackageInfo(context.packageName, android.content.pm.PackageManager.GET_PERMISSIONS)
        assertFalse(info.requestedPermissions.orEmpty().contains("android.permission.INTERNET"))
    }
    @Test fun invalidModelDoesNotCrashJni() {
        val engine = BonsaiEngine()
        try {
            engine.initialize(ModelConfig("/nonexistent/model.gguf", "Q2_0", emptyList()), GenerationSettings())
            fail("Expected model rejection")
        } catch (_: IllegalStateException) { } finally { engine.close() }
    }
    @Test fun keystoreHistoryRoundTripAndDeletion() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val isolated = object : android.content.ContextWrapper(context) {
            override fun getNoBackupFilesDir() = java.io.File(context.cacheDir, "history-instrumentation-test").apply { mkdirs() }
        }
        val store = ChatStore(isolated, "bonsai-local-history-instrumentation-v1")
        val messages = listOf(ChatMessage("user", "Synthetic offline test"))
        store.write(messages)
        assertEquals(messages, store.read())
        store.clear()
        assertTrue(store.read().isEmpty())
    }
    @Test fun closedEngineCannotBeReusedAndCloseIsIdempotent() {
        val engine = BonsaiEngine()
        engine.close()
        engine.close()
        engine.cancel()
        try { engine.prepare(); fail("Closed engine must reject work") }
        catch (_: IllegalStateException) { }
    }
}
