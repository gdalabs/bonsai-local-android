package org.gdalabs.bonsailocal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import android.os.Bundle
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BonsaiDeviceInferenceTest {
    @Test fun officialModelGenerationResetAndCancellation() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val arguments = InstrumentationRegistry.getArguments()
        assumeTrue("Explicit -e runModelTests true required", arguments.getString("runModelTests") == "true")
        val repository = ModelRepository(instrumentation.targetContext)
        val model = repository.installed().firstOrNull { it.manifest.format == "Q2_0" }
        assertNotNull("Import official Q2_0 in Settings before running model tests", model)
        repository.verify(model!!)
        val olderEngine = BonsaiEngine()
        val engine = BonsaiEngine()
        val settings = GenerationSettings(temperature = 0f, maxTokens = 32)
        try {
            engine.initialize(ModelConfig(model.file.absolutePath, model.manifest.format, model.manifest.specialTokens), settings)
            olderEngine.close()
            val messages = listOf(ChatMessage("user", "What is 2 + 2? Answer with just the number."))
            fun generate(): Pair<String, String> {
                engine.prepare()
                val decoder = Utf8Stream()
                val output = StringBuilder()
                val stats = engine.generate(messages, settings) { output.append(decoder.append(it)); true }
                output.append(decoder.append(byteArrayOf(), true))
                return output.toString() to stats
            }
            val first = generate()
            val second = generate()
            assertEquals("Recurrent context must reset between generations", first.first, second.first)
            assertTrue("Expected arithmetic answer from actual inference", first.first.trim().contains("4"))
            instrumentation.sendStatus(0, Bundle().apply { putString("synthetic_output", first.first); putString("runtime_stats", first.second) })
            engine.prepare()
            val cancelled = org.json.JSONObject(engine.generate(messages, settings) { false })
            assertTrue(cancelled.getBoolean("cancelled"))
            assertEquals(1, cancelled.getInt("generated_tokens"))
        } finally { engine.close(); olderEngine.close() }
    }
}
