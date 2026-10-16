
package com.example.nexusnote.di

import android.content.Context
import androidx.room.Room
import com.example.nexusnote.data.local.NexusNoteDatabase

class AppContainer(context: Context) {

    val database: NexusNoteDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            NexusNoteDatabase::class.java,
            "nexusnote_database"
        ).build()
}
