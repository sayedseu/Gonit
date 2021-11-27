package com.dot.gonit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg editor: Editor)

    @Query("DELETE FROM Editor WHERE rowId = :rowId")
    suspend fun delete(vararg rowId: Int): Int

    @Query("DELETE FROM Editor")
    suspend fun delete(): Int

    @Query("SELECT * FROM Editor")
    suspend fun retrieve(): List<Editor>
}