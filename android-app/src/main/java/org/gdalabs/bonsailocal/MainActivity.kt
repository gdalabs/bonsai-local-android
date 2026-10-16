package org.gdalabs.bonsailocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private lateinit var model: ChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = androidx.lifecycle.ViewModelProvider(this)[ChatViewModel::class.java]
        setContent {
            val colors = if (isSystemInDarkTheme()) darkColorScheme(primary = Color(0xFFB1D4A0), primaryContainer = Color(0xFF274C27), onPrimaryContainer = Color(0xFFCFEBBE), secondaryContainer = Color(0xFF344A2F), background = Color(0xFF111610), surface = Color(0xFF171D16), surfaceContainer = Color(0xFF20281E))
                else lightColorScheme(primary = Color(0xFF386A35), primaryContainer = Color(0xFFD9EBD0), onPrimaryContainer = Color(0xFF153513), secondaryContainer = Color(0xFFDDE8D6), background = Color(0xFFF8FAF3), surface = Color(0xFFF8FAF3), surfaceContainer = Color(0xFFEDF1E7))
            MaterialTheme(colorScheme = colors) { BonsaiLocal(model) }
        }
    }
    override fun onStart() { super.onStart(); model.setForeground(true) }
    override fun onResume() { super.onResume(); model.setForeground(true) }
    override fun onStop() { model.setForeground(false); super.onStop() }
    override fun onTrimMemory(level: Int) { super.onTrimMemory(level); if (::model.isInitialized) model.trimMemory(level) }
}


@Composable private fun BonsaiLocal(vm: ChatViewModel) {
    val state by vm.state.collectAsState()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var reset by rememberSaveable { mutableStateOf(false) }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
            Row(Modifier.fillMaxWidth().padding(20.dp, 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_bonsai), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Bonsai Local", style = MaterialTheme.typography.titleLarge)
                    Text("On-device AI · GDA Labs", style = MaterialTheme.typography.labelSmall)
                }
                Text("OFFLINE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            if (state.busy) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(state.status, Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                    if (!state.generating) TextButton(onClick = vm::stop) { Text("Cancel") }
                }
            }
            state.error?.let { error ->
                Card(Modifier.padding(16.dp, 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Something needs attention", style = MaterialTheme.typography.titleSmall)
                        SelectionContainer { Text(error, style = MaterialTheme.typography.bodySmall) }
                        TextButton(onClick = vm::clearError) { Text("Dismiss") }
                    }
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (tab) {
                    0 -> ChatScreen(vm, state, { tab = 1 }, { reset = true })
                    1 -> ModelsScreen(vm, state)
                    else -> SettingsScreen(vm, state) { reset = true }
                }
            }
            NavigationBar {
                listOf("Chat", "Models", "Settings").forEachIndexed { index, label ->
                    NavigationBarItem(selected = tab == index, onClick = { tab = index }, label = { Text(label) },
                        icon = { Icon(painterResource(listOf(R.drawable.ic_chat, R.drawable.ic_models, R.drawable.ic_settings)[index]), contentDescription = null) })
                }
            }
        }
        if (reset) AlertDialog(onDismissRequest = { reset = false }, title = { Text("Clear this conversation?") },
            text = { Text("Messages stored on this device will be deleted.") },
            confirmButton = { TextButton(onClick = { vm.reset(); reset = false }, enabled = !state.busy) { Text("Clear") } },
            dismissButton = { TextButton(onClick = { reset = false }) { Text("Keep") } })
    }
}

@Composable private fun ChatScreen(vm: ChatViewModel, state: UiState, models: () -> Unit, reset: () -> Unit) {
    var draft by rememberSaveable { mutableStateOf("") }
    var metrics by rememberSaveable { mutableStateOf(false) }
    val list = rememberLazyListState()
    val atBottom by remember { derivedStateOf { !list.canScrollForward } }
    LaunchedEffect(state.messages.size, state.messages.lastOrNull()?.content) {
        if (atBottom && state.messages.isNotEmpty()) list.scrollToItem(state.messages.lastIndex)
    }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        if (state.messages.isEmpty()) {
            Column(Modifier.weight(1f).fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.Center) {
                Text("A little AI.\nEntirely yours.", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(14.dp))
                Text("Ask, write, or explore with Bonsai 2 2B. Your conversation stays on this phone.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(24.dp))
                if (!state.ready) {
                    val installed = state.models.firstOrNull { it.manifest.format == "Q2_0" } ?: state.models.firstOrNull()
                    Button(onClick = { if (installed == null) models() else vm.load(installed) }, enabled = !state.busy) {
                        Text(if (installed == null) "Choose a local model" else "Load model & start")
                    }
                    Text(if (installed == null) "The bundled APK prepares its model on first launch. The small APK needs a local GGUF." else "Model installed. No import needed.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 10.dp))
                } else {
                    listOf("Explain why the sky is blue.", "Help me write a thank-you note.").forEach { prompt ->
                        OutlinedButton(onClick = { draft = prompt }) { Text(prompt) }
                    }
                }
            }
        } else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(if (state.ready) "Private, on-device chat" else "Load a model to continue", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                TextButton(onClick = reset, enabled = !state.busy) { Text("New chat") }
            }
            if (!state.ready && !state.busy) OutlinedButton(onClick = { state.models.firstOrNull()?.let(vm::load) ?: models() }) { Text("Load model") }
            LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = list, contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(state.messages) { _, message ->
                    val user = message.role == "user"
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = if (user) Alignment.End else Alignment.Start) {
                        Text(if (user) "You" else "Bonsai", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(6.dp, 3.dp))
                        Card(Modifier.widthIn(max = 600.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (user) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer)) {
                            SelectionContainer { Text(message.content.ifEmpty { "Thinking…" }, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge) }
                        }
                    }
                }
            }
        }
        if (runCatching { org.json.JSONObject(state.stats.substringBefore('\n')).optString("finish_reason") == "length" }.getOrDefault(false)) {
            Text("The output token limit was reached. Increase the limit for your next reply, or ask Bonsai to continue. This response may be incomplete.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Output limit", style = MaterialTheme.typography.labelSmall)
            listOf(256, 512, 1024).forEach { value ->
                FilterChip(state.settings.maxTokens == value, { vm.outputLimit(value) }, label = { Text(value.toString()) }, enabled = !state.busy)
            }
        }
        Text("A higher limit allows longer replies and takes more time. It applies to the next reply.", style = MaterialTheme.typography.bodySmall)
        if (state.stats.isNotBlank()) {
            TextButton(onClick = { metrics = !metrics }) { Text("Session performance") }
            if (metrics) Text(performanceText(state.stats), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
        }
        OutlinedTextField(draft, { if (it.length <= 16384) draft = it }, label = { Text("Ask Bonsai") }, modifier = Modifier.fillMaxWidth(), maxLines = 4, shape = RoundedCornerShape(18.dp))
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.End) {
            if (state.generating) Button(onClick = vm::stop) { Text("Stop generating") }
            else Button(onClick = { vm.send(draft); draft = "" }, enabled = state.ready && !state.busy && draft.isNotBlank()) { Text("Send") }
        }
    }
}

private fun performanceText(raw: String): String = runCatching {
    val data = org.json.JSONObject(raw.substringBefore('\n'))
    val count = data.getInt("generated_tokens")
    val first = if (count > 0) "%.1f s".format(data.getDouble("ttft_ms") / 1000) else "—"
    val speed = if (count > 1) "%.2f tok/s".format(data.getDouble("tokens_per_second")) else "—"
    "Load %.1f s · First token %s · %s\n%d output tokens · Peak process RSS %.0f MiB".format(
        data.getDouble("load_ms") / 1000, first, speed, count, data.getLong("process_peak_rss_kib") / 1024.0)
}.getOrDefault(raw)

@Composable private fun ModelsScreen(vm: ChatViewModel, state: UiState) {
    val context = LocalContext.current
    val manifests = remember { vm.repository.officialManifests().sortedBy { if (it.first == "bonsai-Q2_0.json") 0 else 1 } }
    var manifest by rememberSaveable { mutableStateOf(manifests.firstOrNull()?.second ?: "") }
    var advanced by rememberSaveable { mutableStateOf(false) }
    var deletion by remember { mutableStateOf<InstalledModel?>(null) }
    val pickModel = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) vm.import(uri, manifest) }
    val pickManifest = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching {
            val text = context.contentResolver.openInputStream(uri)!!.use { input ->
                val out = java.io.ByteArrayOutputStream()
                val part = ByteArray(4096)
                while (out.size() <= 65536) {
                    val n = input.read(part, 0, minOf(part.size, 65537 - out.size()))
                    if (n < 0) break
                    out.write(part, 0, n)
                }
                require(out.size() <= 65536) { "Manifest exceeds 64 KiB" }
                out.toByteArray().toString(Charsets.UTF_8)
            }
            org.gdalabs.bonsailocal.model.ModelManifest.parse(text); manifest = text
        }.onFailure { android.widget.Toast.makeText(context, it.message, android.widget.Toast.LENGTH_LONG).show() }
    }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
        item { Text("Your models", style = MaterialTheme.typography.headlineMedium); Text("Official Bonsai 2 2B · CPU inference", style = MaterialTheme.typography.bodySmall) }
        if (state.models.isEmpty()) item { Text("No model installed. Use the model-bundled APK, or import a local GGUF below.") }
        itemsIndexed(state.models) { _, model ->
            val active = state.selected == model.manifest.sha256 && state.ready
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) {
                Text(model.manifest.name, style = MaterialTheme.typography.titleMedium)
                Text("${model.manifest.format} · %.1f MB".format(model.manifest.size / 1_000_000.0), style = MaterialTheme.typography.bodySmall)
                Text(if (active) "Loaded · ready to chat" else "Installed · verified again when loading", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { if (active) vm.unload() else vm.load(model) }, enabled = !state.busy) { Text(if (active) "Unload" else "Load model") }
                    TextButton(onClick = { deletion = model }, enabled = !state.busy) { Text("Remove") }
                }
            } }
        }
        item { TextButton(onClick = { advanced = !advanced }) { Text("Import or replace a model") } }
        if (advanced) {
            item { Text("Choose the matching format, then select a GGUF already stored on this phone. The app never downloads models.") }
            itemsIndexed(manifests) { _, (name, text) -> FilterChip(selected = manifest == text, onClick = { manifest = text }, label = { Text(name.removePrefix("bonsai-").removeSuffix(".json")) }, enabled = !state.busy) }
            item { Text(runCatching { org.gdalabs.bonsailocal.model.ModelManifest.parse(manifest).name }.getOrDefault(""), style = MaterialTheme.typography.bodySmall) }
            item { Button(onClick = { pickModel.launch(arrayOf("*/*")) }, enabled = !state.busy && manifest.isNotBlank()) { Text("Select GGUF file") } }
            item { TextButton(onClick = { pickManifest.launch(arrayOf("application/json", "application/octet-stream")) }, enabled = !state.busy) { Text("Use custom model manifest") }; Text("A custom checksum checks integrity, not publisher authenticity. Unsupported models are rejected.", style = MaterialTheme.typography.bodySmall) }
        }
    }
    deletion?.let { model -> AlertDialog(onDismissRequest = { deletion = null }, title = { Text("Remove this model?") }, text = { Text(model.manifest.name) },
        confirmButton = { TextButton(onClick = { vm.delete(model); deletion = null }, enabled = !state.busy) { Text("Remove") } }, dismissButton = { TextButton(onClick = { deletion = null }) { Text("Keep") } }) }
}

@Composable private fun SettingsScreen(vm: ChatViewModel, state: UiState, reset: () -> Unit) {
    var advanced by rememberSaveable { mutableStateOf(false) }
    var notices by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val enabled = !state.busy && !state.ready
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
        item { Text("Settings", style = MaterialTheme.typography.headlineMedium) }
        item { Text("Conversation", style = MaterialTheme.typography.titleMedium); Text("History is encrypted on this device. No account or network connection is used. An unlocked or compromised phone can expose data."); OutlinedButton(onClick = reset, enabled = !state.busy) { Text("Clear conversation") } }
        item { Text("Memory & generation", style = MaterialTheme.typography.titleMedium); Text("Unload the model before changing these settings. Start with 2K context and 2 threads.", style = MaterialTheme.typography.bodySmall) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(2048,4096).forEach { value -> FilterChip(state.settings.context == value, { vm.settings(state.settings.copy(context = value)) }, label = { Text(if (value == 2048) "2K" else "4K") }, enabled = enabled) } } }
        item { SettingSlider("Threads", state.settings.threads.toFloat(), 1f..8f, enabled) { vm.settings(state.settings.copy(threads = it.toInt())) } }
        item { Row(verticalAlignment = Alignment.CenterVertically) { Switch(state.settings.memoryEfficient, { vm.settings(state.settings.copy(memoryEfficient = it)) }, enabled = enabled); Spacer(Modifier.width(8.dp)); Text("Memory-efficient mode") } }
        item { SettingSlider("Maximum output tokens", state.settings.maxTokens.toFloat(), 32f..1024f, !state.busy) { vm.outputLimit(it.toInt()) }; Text("Can be changed while the model is loaded. Prompt and output must fit the context window.", style = MaterialTheme.typography.bodySmall) }
        item { TextButton(onClick = { advanced = !advanced }) { Text("Advanced generation settings") } }
        if (advanced) {
            item { SettingSlider("Temperature", state.settings.temperature, 0f..2f, enabled) { vm.settings(state.settings.copy(temperature = it)) } }
            item { SettingSlider("Top-p", state.settings.topP, 0.05f..1f, enabled) { vm.settings(state.settings.copy(topP = it)) } }
            item { SettingSlider("Top-k", state.settings.topK.toFloat(), 1f..200f, enabled) { vm.settings(state.settings.copy(topK = it.toInt())) } }
            item { Row(verticalAlignment = Alignment.CenterVertically) { Switch(state.settings.thinking, { vm.settings(state.settings.copy(thinking = it)) }, enabled = enabled); Spacer(Modifier.width(8.dp)); Text("Thinking mode") }; Text("Thinking can use the short output budget before an answer appears.", style = MaterialTheme.typography.bodySmall) }
        }
        item { HorizontalDivider(); Text("Bonsai Local · ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.titleMedium); Text("An independent Android app by GDA Labs. Official model by PrismML. No affiliation or endorsement is implied."); TextButton(onClick = { notices = !notices }) { Text("Open-source licenses") } }
        if (notices) item { SelectionContainer { Text(remember { context.assets.open("THIRD_PARTY.txt").bufferedReader().use { it.readText() } }, style = MaterialTheme.typography.bodySmall) } }
    }
}

@Composable private fun SettingSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, enabled: Boolean, change: (Float) -> Unit) {
    Column { Text("$label: ${"%.2f".format(value)}"); Slider(value, change, valueRange = range, enabled = enabled) }
}
