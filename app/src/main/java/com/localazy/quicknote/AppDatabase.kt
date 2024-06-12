package com.localazy.quicknote

import androidx.room.Database
import androidx.room.RoomDatabase
import com.localazy.quicknote.notes.Note
import com.localazy.quicknote.notes.NotesDao

@Database(
    entities = [Note::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notes(): NotesDao
}
