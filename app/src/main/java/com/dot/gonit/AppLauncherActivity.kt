package com.dot.gonit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppLauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            Intent(this@AppLauncherActivity, MainActivity::class.java).also {
                delay(1500L)
                startActivity(it)
                finish()
            }
        }
    }
}