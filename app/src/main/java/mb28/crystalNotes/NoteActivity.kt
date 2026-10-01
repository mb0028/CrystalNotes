package mb28.crystalNotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.net.toUri
import dev.jeziellago.compose.markdowntext.MarkdownText
import mb28.crystalNotes.core.Note
import mb28.crystalNotes.ui.EditTabPopup
import mb28.crystalNotes.ui.NewTabPopup
import mb28.crystalNotes.ui.theme.CrystalNotesTheme
import mb28.monoP.icons.add_2

const val EXTRA_PATH = "EXTRA_PATH"

class NoteActivity : ComponentActivity() {
    lateinit var note: Note
    var currentTab by mutableIntStateOf(0)
    var saved by mutableStateOf(true)
    var mdView by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        val notePath = intent.getStringExtra(EXTRA_PATH)
        if (notePath == null) finish()
        note = Note.load(notePath!!)

        setContent {
            CrystalNotesTheme {
                Scaffold(
                    Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar()
                    }
                ) { innerPadding ->
                    if (mdView) {
                        LazyColumn(
                            contentPadding = innerPadding.plus(
                                PaddingValues(10.dp, 10.dp, 10.dp, 500.dp)
                            )
                        ) {
                            item {
                                MarkdownText(
                                    note.tabs[currentTab],
                                    modifier = Modifier.fillMaxSize(),
                                    isTextSelectable = true,
                                    onLinkClicked = { url ->
                                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }
                                )
                            }

                        }
                    } else {
                        OutlinedTextField(
                            note.tabs[currentTab],
                            {
                                note.tabs[currentTab] = it
                                saved = false
                            },
                            minLines = 15,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(5.dp),
                            shape = RoundedCornerShape(35.dp)
                        )
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        note.saveChanges()
        saved = true
    }

    override fun onDestroy() {
        super.onDestroy()
        note.saveChanges()
        saved = true
    }

    @Composable
    private fun TopAppBar() {
        Column {
            TopAppBar(
                {
                    OutlinedTextField(
                        note.name.value,
                        {
                            note.name.value = it
                            saved = false
                        },
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                navigationIcon = {
                    BadgedBox({
                        if (!saved) {
                            Badge {
                                Text("not saved")
                            }
                        }
                    }) {
                        IconButton(
                            { finish() },
                            colors = IconButtonDefaults.iconButtonColors(
                                MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.padding(start = 15.dp)
                        ) { Icon(painterResource(R.drawable.arrow_back_24px), null) }
                    }
                },
                actions = {
                    var newTabPopup by remember { mutableStateOf(false) }
                    IconButton(
                        { newTabPopup = true },
                    ) { Icon(add_2, null) }
                    IconButton({ mdView = !mdView }) {
                        Icon(
                            painterResource(if (mdView) R.drawable.draw_24px else R.drawable.markdown_24px),
                            null
                        )
                    }

                    if (newTabPopup) {
                        NewTabPopup(note) {
                            newTabPopup = false
                            if (it) note.saveChanges()
                        }
                    }
                }
            )
            PrimaryScrollableTabRow(
                currentTab,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                note.tabNames.fastForEachIndexed { i, tabName ->
                    var editPopup by remember { mutableStateOf(false) }
                    Tab(
                        i == currentTab,
                        { currentTab = i },
                        text = {
                            Row(
                                Modifier.height(30.dp).offset((-10).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    { editPopup = true },
                                ) { Text(note.tabIcons[i]) }
                                Text(tabName)
                            }
                        }
                    )

                    if (editPopup) {
                        EditTabPopup(note, i) {
                            editPopup = false
                            if (it) note.saveChanges()
                        }
                    }
                }
            }
        }
    }
}












