package com.dot.gonit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.tiagohm.markdownview.css.styles.Github
import com.dot.gonit.databinding.ActivityMarkDownViewBinding

class MarkDownViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMarkDownViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkDownViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.markDownToolbar)
        supportActionBar?.setTitle(R.string.doc)
        binding.markDownToolbar.setNavigationIcon(R.drawable.ic_back)
        binding.markDownToolbar.setNavigationOnClickListener { finish() }

        binding.markDownView.addStyleSheet(Github())
        binding.markDownView.loadMarkdownFromAsset("Documentation.md")
    }
}