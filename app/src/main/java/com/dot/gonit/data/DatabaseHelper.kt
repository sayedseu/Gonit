package com.dot.gonit.data

interface DatabaseHelper {
    suspend fun insert(editor: Editor)
    suspend fun delete(rowId: Int): Int
    suspend fun delete(): Int
    suspend fun retrieve(): List<Editor>
}