package mb28.crystalNotes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mb28.crystalNotes.core.Note

@Composable
fun NewTabPopup(note: Note, onDismiss: (Boolean) -> Unit) {
    var name by remember { mutableStateOf("New tab") }
    var icon by remember { mutableStateOf("🎨") }
    AlertDialog(
        { onDismiss(false) },
        {
            Button({
                note.newTab(name, icon)
                onDismiss(true)
            }) {
                Text("Create")
            }
        },
        Modifier.fillMaxWidth(),
        dismissButton = {
            OutlinedButton({ onDismiss(false) }) {
                Text("Cancel")
            }
        },
        title = {
            Text("New Tab")
        },
        text = {
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    name,
                    {name = it},
                    Modifier.fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(20.dp),
                    label = {
                        Text("Tab name")
                    }
                )
                Spacer(Modifier.width(5.dp))
                OutlinedTextField(
                    icon,
                    {icon = it},
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    label = {
                        Text("Icon")
                    }
                )
            }
        }
    )
}

@Composable
fun EditTabPopup(note: Note, index: Int, onDismiss: (Boolean) -> Unit) {
    var deletePopup by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(note.tabNames[index]) }
    var icon by remember { mutableStateOf(note.tabIcons[index]) }
    AlertDialog(
        { onDismiss(false) },
        {
            Button({
                note.tabNames[index] = name
                note.tabIcons[index] = icon
                onDismiss(true)
            }) {
                Text("Save")
            }
        },
        Modifier.fillMaxWidth(),
        dismissButton = {
            FilledTonalButton({ deletePopup = true }) {
                Text("Delete Tab")
            }
        },
        title = {
            Text("Edit Tab")
        },
        text = {
            Column {
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        name,
                        {name = it},
                        Modifier.fillMaxWidth(0.7f),
                        shape = RoundedCornerShape(20.dp),
                        label = {
                            Text("Tab name")
                        }
                    )
                    Spacer(Modifier.width(5.dp))
                    OutlinedTextField(
                        icon,
                        {icon = it},
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        label = {
                            Text("Icon")
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                    Button({
                        note.apply {
                            tabNames.add(index - 1, tabNames.removeAt(index))
                            tabIcons.add(index - 1, tabIcons.removeAt(index))
                            tabs.add(index - 1, tabs.removeAt(index))
                        }
                        onDismiss(true)
                    }, enabled = index > 0) { Text("Move Left") }
                    Spacer(Modifier.width(5.dp))
                    Button({
                        note.apply {
                            tabNames.add(index + 1, tabNames.removeAt(index))
                            tabIcons.add(index + 1, tabIcons.removeAt(index))
                            tabs.add(index + 1, tabs.removeAt(index))
                        }
                        onDismiss(true)
                    }, enabled = index + 1 < note.tabs.count()) { Text("Move Right") }
                }
            }

        }
    )
    if (deletePopup) {
        DeleteTabPopup(note, index) {
            deletePopup = false
            if (it) {
                onDismiss(true)
            }
        }
    }
}

@Composable
fun DeleteTabPopup(note: Note, index: Int, onDismiss: (Boolean) -> Unit) {
    AlertDialog(
        { onDismiss(false) },
        {
            Button({
                note.tabNames.removeAt(index)
                note.tabIcons.removeAt(index)
                note.tabs.removeAt(index)
                onDismiss(true)
            }) {
                Text("Delete")
            }
        },
        Modifier.fillMaxWidth(),
        dismissButton = {
            OutlinedButton({ onDismiss(false) }) {
                Text("Cancel")
            }
        },
        title = {
            Text("Delete Tab?")
        },
        text = {
            Text(note.tabNames[index])
        }
    )
}
