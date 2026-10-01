package mb28.crystalNotes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mb28.crystalNotes.core.Note
import mb28.crystalNotes.core.Settings
import mb28.crystalNotes.ui.theme.CrystalNotesTheme
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    var allFileAccessGranted by mutableStateOf(Environment.isExternalStorageManager())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        Settings.load(this)

        setContent {
            CrystalNotesTheme {
                Scaffold(
                    Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ) { paddingValues ->
                    when {
                        !allFileAccessGranted -> {
                            Column(
                                Modifier.fillMaxSize(),
                                Arrangement.Center,
                                Alignment.CenterHorizontally
                            ) {
                                Text("All files access is denied")
                                Spacer(Modifier.height(10.dp))
                                Text("(┬┬﹏┬┬)", fontSize = 50.sp)
                                Spacer(Modifier.height(20.dp))
                                Button({
                                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                        .setData("package:$packageName".toUri())
                                    startActivity(intent)
                                }) {
                                    Text("Grant")
                                }
                            }
                        }

                        !File(Settings.appDir).exists() -> {
                            Column(
                                Modifier.fillMaxSize(),
                                Arrangement.Center,
                                Alignment.CenterHorizontally
                            ) {
                                var newPath by remember { mutableStateOf("${Environment.getExternalStorageDirectory().path}/Download") }

                                Text("Select a folder to read notes from\nYou can change paddingValues later", textAlign = TextAlign.Center)
                                Spacer(Modifier.height(10.dp))
                                Text("📂", fontSize = 50.sp)
                                Spacer(Modifier.height(20.dp))
                                TextField(
                                    newPath,
                                    { newPath = it },
                                    minLines = 3,
                                    maxLines = 5
                                )
                                Spacer(Modifier.height(20.dp))
                                Button({
                                   if (File(newPath).exists()) {
                                       Settings.appDir = newPath
                                       Settings.save(this@MainActivity)
                                   }
                                }) {
                                    Text("Continue")
                                }
                            }
                        }

                        else -> {
                            val notes = remember { mutableStateListOf<File>() }

                            LaunchedEffect(Unit) {
                                withContext(Dispatchers.IO) {
                                    val allFiles = File(Settings.appDir).listFiles { file ->
                                        file.path.endsWith(Note.EXTENSION)
                                    } ?: arrayOf()
                                    allFiles.sortBy { it.name }
                                    notes.clear()
                                    delay(50.milliseconds)
                                    notes.addAll(allFiles)
                                }
                            }

                            LazyColumn(
                                Modifier.fillMaxSize().padding(10.dp),
                                contentPadding = paddingValues
                            ) {
                                val count = notes.count()
                                items(count) { i ->
                                    SegmentedListItem(
                                        shapes = ListItemDefaults.segmentedShapes(i, count),
                                        colors = ListItemDefaults.segmentedColors(
                                            MaterialTheme.colorScheme.surfaceBright
                                        ),
                                        modifier = Modifier.padding(bottom = 2.dp),
                                        onClick = {
                                            val intent = Intent(this@MainActivity, NoteActivity::class.java)
                                                .putExtra(EXTRA_PATH, notes[i].path)
                                            startActivity(intent)
                                        }
                                    ) {
                                        Text(notes[i].nameWithoutExtension)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            allFileAccessGranted = Environment.isExternalStorageManager()
        }
    }
}
