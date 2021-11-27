package com.dot.gonit.data

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    override suspend fun insert(editor: Editor) = appDatabase.appDao().insert(editor)
    override suspend fun delete(rowId: Int) = appDatabase.appDao().delete(rowId)
    override suspend fun delete() = appDatabase.appDao().delete()
    override suspend fun retrieve() = appDatabase.appDao().retrieve()
}