package com.adx.aiassistant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val Navy = Color(0xFF07111F)
private val Panel = Color(0xFF141F34)
private val Blue = Color(0xFF36A9E1)
private val Txt = Color(0xFFF2F5FA)
private val Muted = Color(0xFF9AA9C0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("adx", Context.MODE_PRIVATE)
        setContent { ADXApp(prefs) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ADXApp(prefs: SharedPreferences) {
    var screen by remember { mutableStateOf("home") }
    var drawer by remember { mutableStateOf(false) }
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Navy) {
            Box {
                when (screen) {
                    "home" -> Home(prefs, { drawer = true }, { screen = it })
                    "chat" -> Chat(prefs) { screen = "home" }
                    "memory" -> Memory(prefs) { screen = "home" }
                    "settings" -> Settings(prefs) { screen = "home" }
                    "capabilities" -> Capabilities { screen = "home" }
                    "tools" -> Tools { screen = "home" }
                    "rules" -> Rules(prefs) { screen = "home" }
                }
                if (drawer) Drawer({ drawer = false }) { drawer = false; screen = it }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(prefs: SharedPreferences, menu: () -> Unit, nav: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val speech = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        val text = r.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!text.isNullOrBlank()) Toast.makeText(context, "ADX heard: $text", Toast.LENGTH_SHORT).show()
    }
    fun speak() = speech.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    })
    Scaffold(containerColor = Navy, topBar = {
        TopAppBar(title = { Text("ADX AI", color = Txt, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = menu) { Icon(Icons.Default.Menu, "Menu", tint = Txt) } }, actions = { IconButton(onClick = { nav("settings") }) { Icon(Icons.Default.Settings, "Settings", tint = Txt) } })
    }, bottomBar = {
        Row(Modifier.fillMaxWidth().background(Panel).navigationBarsPadding().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            NavItem("Home", Icons.Default.Home) { nav("home") }
            NavItem("Tools", Icons.Default.Tune) { nav("tools") }
            IconButton(Modifier.size(62.dp), onClick = { speak() }) { Icon(Icons.Default.Mic, "Voice", tint = Blue, modifier = Modifier.size(40.dp)) }
            NavItem("Memory", Icons.Default.Memory) { nav("memory") }
            NavItem("Chat", Icons.Default.Chat) { nav("chat") }
        }
    }) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Good day,", color = Muted, style = MaterialTheme.typography.titleLarge); Text("ADX is ready.", color = Txt, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold) }
            item { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF102448)), shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(18.dp)) { Text("Advanced AI Assistant", color = Txt, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("Voice • Memory • Vision • Automation • Research", color = Muted); Spacer(Modifier.height(12.dp)); Button(onClick = { nav("capabilities") }) { Text("View all capabilities") } } } }
            item { CardItem("Ask ADX anything", "Chat with your configured AI provider", Icons.Default.AutoAwesome) { nav("chat") } }
            item { CardItem("Voice assistant", "Hindi / English voice input", Icons.Default.Mic) { speak() } }
            item { CardItem("Phone tools", "Supported phone actions and utilities", Icons.Default.Smartphone) { nav("tools") } }
            item { CardItem("Memories", "Store and review local assistant memories", Icons.Default.Memory) { nav("memory") } }
            item { CardItem("Rules & permissions", "Control ADX behaviour and safety", Icons.Default.Security) { nav("rules") } }
        }
    }
}

@Composable fun NavItem(t: String, i: androidx.compose.ui.graphics.vector.ImageVector, a: () -> Unit) { Column(Modifier.clickable(onClick = a), horizontalAlignment = Alignment.CenterHorizontally) { Icon(i, t, tint = Muted); Text(t, color = Muted, style = MaterialTheme.typography.labelSmall) } }
@Composable fun CardItem(t: String, d: String, i: androidx.compose.ui.graphics.vector.ImageVector, a: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = a), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Icon(i, t, tint = Blue, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(14.dp)); Column { Text(t, color = Txt, fontWeight = FontWeight.Bold); Text(d, color = Muted) } } } }

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Page(title: String, back: () -> Unit, content: @Composable ColumnScope.() -> Unit) { Scaffold(containerColor = Navy, topBar = { TopAppBar(title = { Text(title, color = Txt) }, navigationIcon = { IconButton(onClick = back) { Icon(Icons.Default.ArrowBack, "Back", tint = Txt) } }) }) { p -> Column(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content) } }

@Composable fun Chat(prefs: SharedPreferences, back: () -> Unit) { var input by remember { mutableStateOf("") }; var messages by remember { mutableStateOf(listOf("Hi! I am ADX. Configure an AI provider in Settings to start.")) }; Page("ADX Chat", back) { LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) { items(messages.size) { Text(messages[it], color = Txt, modifier = Modifier.background(Panel, RoundedCornerShape(16.dp)).padding(14.dp)) } }; Row(verticalAlignment = Alignment.CenterVertically) { OutlinedTextField(input, { input = it }, Modifier.weight(1f), label = { Text("Message ADX") }); IconButton(onClick = { if (input.isNotBlank()) { messages = messages + "You: $input"; input = "" } }) { Icon(Icons.Default.Send, "Send", tint = Blue) } } }
@Composable fun Memory(prefs: SharedPreferences, back: () -> Unit) { var text by remember { mutableStateOf(prefs.getString("memory", "") ?: "") }; Page("Memories", back) { Text("Local memory", color = Txt, fontWeight = FontWeight.Bold); OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth(), minLines = 5, label = { Text("Memory") }); Button(onClick = { prefs.edit().putString("memory", text).apply() }) { Text("Save memory") } }
@Composable fun Settings(prefs: SharedPreferences, back: () -> Unit) { var endpoint by remember { mutableStateOf(prefs.getString("endpoint", "https://api.openai.com/v1") ?: "") }; Page("Settings", back) { Text("AI Provider", color = Txt, fontWeight = FontWeight.Bold); OutlinedTextField(endpoint, { endpoint = it }, Modifier.fillMaxWidth(), label = { Text("OpenAI-compatible endpoint") }); Button(onClick = { prefs.edit().putString("endpoint", endpoint).apply() }) { Text("Save settings") }; Text("Permissions", color = Txt, fontWeight = FontWeight.Bold); Text("Microphone and other sensitive actions remain permission-gated.", color = Muted) } }
@Composable fun Capabilities(back: () -> Unit) { Page("All Capabilities", back) { listOf("Voice assistant", "Chat", "Local memory", "Vision foundation", "Research tools", "Coding tools", "Phone automation", "Notifications", "Safety rules").forEach { CardItem(it, "Available in ADX", Icons.Default.AutoAwesome) {} } } }
@Composable fun Tools(back: () -> Unit) { Page("Tools", back) { listOf("Voice input", "Phone utilities", "Research", "Coding", "Documents", "Markets").forEach { CardItem(it, "Open tool", Icons.Default.Tune) {} } } }
@Composable fun Rules(prefs: SharedPreferences, back: () -> Unit) { var safe by remember { mutableStateOf(true) }; Page("ADX Rules", back) { Row(verticalAlignment = Alignment.CenterVertically) { Text("Safety confirmations", color = Txt, modifier = Modifier.weight(1f)); Switch(safe, { safe = it }) }; Text("Sensitive actions should require Android permissions and user confirmation.", color = Muted) } }

@Composable fun Drawer(close: () -> Unit, nav: (String) -> Unit) { Box(Modifier.fillMaxSize().background(Color(0xAA000000))) { Column(Modifier.fillMaxHeight().width(300.dp).background(Panel).padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("ADX AI", color = Txt, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("Advanced Assistant", color = Muted); listOf("Home" to "home", "Chat" to "chat", "Memories" to "memory", "Tools" to "tools", "Capabilities" to "capabilities", "Rules" to "rules", "Settings" to "settings").forEach { (t,s) -> Text(t, color = Txt, modifier = Modifier.fillMaxWidth().clickable { nav(s) }.padding(14.dp)) }; Spacer(Modifier.weight(1f)); Text("Close", color = Blue, modifier = Modifier.clickable(onClick = close).padding(14.dp)) } } }
