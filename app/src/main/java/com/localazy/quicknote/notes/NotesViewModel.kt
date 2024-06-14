package com.localazy.quicknote.notes

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localazy.quicknote.receiver.NOTES_RECEIVER_ACTION
import com.localazy.quicknote.receiver.NotesReceiver
import kotlinx.coroutines.launch

class NotesViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val context: Context get() = application.applicationContext
    private val db = NotesDb(context)

    var notes by mutableStateOf(listOf<Note>())
        private set

    private val updateReceiver = NotesReceiver { loadItemsFromDb() }

    // Load initial data from Room asynchronously.
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                updateReceiver, IntentFilter(NOTES_RECEIVER_ACTION), Context.RECEIVER_EXPORTED
            )
        }
        loadItemsFromDb()
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(updateReceiver)
    }

    private fun loadItemsFromDb() {
        db.list {
            viewModelScope.launch { notes = it }
        }
    }

    fun addNote(note: String) {
        // Generate ID in a simple way - from timestamp.
        val noteObj = Note((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), note)
        db.insert(noteObj)
        notes = notes + listOf(noteObj)
    }

    fun removeNote(note: Note) {
        notes = notes - listOf(note).toSet()
        db.remove(note)
    }
}
