package mb28.crystalNotes.core

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit


object Settings {
    private const val CRYSTAL_SETTINGS = "CrystalSettings"
    private const val SETTING_APP_DIR = "SETTING_APP_DIR"
    var appDir by mutableStateOf("")


    fun load(context: Context) {
        val sp = context.getSharedPreferences(CRYSTAL_SETTINGS, MODE_PRIVATE)
        appDir = sp.getString(SETTING_APP_DIR, "")!!
    }

    fun save(context: Context) {
        context.getSharedPreferences(CRYSTAL_SETTINGS, MODE_PRIVATE)
            .edit {
                putString(SETTING_APP_DIR, appDir)
            }
    }

}
