package com.dot.gonit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dot.gonit.data.DatabaseHelper
import com.dot.gonit.data.Editor
import com.dot.gonit.data.Resource
import kotlinx.coroutines.Dispatchers

class MainActivityViewModel(private val databaseHelper: DatabaseHelper) : ViewModel() {
    fun insert(editor: Editor) = liveData(Dispatchers.IO) {
        try {
            databaseHelper.insert(editor)
            emit(Resource.success(true))
        } catch (e: Exception) {
            emit(Resource.error("", false))
        }
    }

    fun delete(rowId: Int) = liveData(Dispatchers.IO) {
        try {
            val result = databaseHelper.delete(rowId)
            emit(Resource.success(result))
        } catch (e: Exception) {
            emit(Resource.error("", null))
        }
    }

    fun delete() = liveData(Dispatchers.IO) {
        try {
            val result = databaseHelper.delete()
            emit(Resource.success(result))
        } catch (e: Exception) {
            emit(Resource.error("", null))
        }
    }

    fun retrieve() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            val data = databaseHelper.retrieve()
            emit(Resource.success(data))
        } catch (e: Exception) {
            emit(Resource.error("", null))
        }
    }
}