package com.dot.gonit.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Editor(
    @PrimaryKey val rowId: Int,
    @ColumnInfo(name = "text") val text: String?
)
