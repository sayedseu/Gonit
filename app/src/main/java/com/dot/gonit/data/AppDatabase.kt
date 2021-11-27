package com.dot.gonit.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Editor::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}