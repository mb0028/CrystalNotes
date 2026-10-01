package mb28.crystalNotes.core

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonNames
import java.io.File

@Serializable
@JsonIgnoreUnknownKeys
@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalSerializationApi::class)
class Note {
    @Serializable(with = MutableStateSerializer::class)
    var version = mutableStateOf(1)
    @Serializable(with = MutableStateSerializer::class)
    var name = mutableStateOf("New note")
    @Serializable(with = MutableStateSerializer::class)
    var dateCreated = mutableStateOf(System.currentTimeMillis().toString())
    @Serializable(with = MutableStateSerializer::class)
    var dateModified = mutableStateOf(dateCreated.value)

    @JsonNames("noteFilePath")
    var path = ""

    @Serializable(with = SnapshotStateListSerializer::class)
    var tabIcons = mutableStateListOf("🎨")

    @Serializable(with = SnapshotStateListSerializer::class)
    var tabNames = mutableStateListOf("First Tab")

    @Serializable(with = SnapshotStateListSerializer::class)
    var tabs = mutableStateListOf("# New tab")

    fun newTab(name: String, icon: String = "🎨") {
        tabNames.add(name)
        tabIcons.add(icon)
        tabs.add("# Tab ${tabNames.count()}")
        saveChanges()
    }

    fun saveChanges() {
        dateModified.value = System.currentTimeMillis().toString()
        File(path).writeText(Json.encodeToString(this))
    }

    companion object{
        const val EXTENSION = "crynote"

        fun load(path: String) : Note {
            val file = File(path)
            val raw = file.readText()
            file.copyRecursively(File("$path.backup"), true)

            val note = Json.decodeFromString<Note>(raw).apply {
                this.path = path
                updateFormatVersion()
            }

            return note
        }

    }

    private fun updateFormatVersion() {
        if (version.value == 1) {
            tabIcons.clear()
            tabNames.forEach { _ ->
                tabIcons.add("🎨")
            }
            version.value = 2
            saveChanges()
        }
    }
}