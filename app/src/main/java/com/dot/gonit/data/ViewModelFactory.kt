package com.dot.gonit.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dot.gonit.MainActivityViewModel

class ViewModelFactory(private val databaseHelper: DatabaseHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(databaseHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}