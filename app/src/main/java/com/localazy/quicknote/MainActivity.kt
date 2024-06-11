package com.localazy.quicknote

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import com.localazy.quicknote.notes.NotesViewModel
import com.localazy.quicknote.ui.AddNote
import com.localazy.quicknote.ui.ShowNotes

class MainActivity : ComponentActivity() {

    private val notesViewModel by viewModels<NotesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) startFloatingService()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startFloatingService()
        }

        setContent {
            Column {
                AddNote(getString(R.string.add_note)) {
                    notesViewModel.addNote(it)
                }
                ShowNotes(notesViewModel.notes) {
                    notesViewModel.removeNote(it)
                }
            }
        }
    }
}
