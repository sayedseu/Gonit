package com.dot.gonit

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dot.gonit.data.*
import com.dot.gonit.databinding.ActivityMainBinding
import com.dot.gonit.features.operators.Operators.calculateAvg
import com.dot.gonit.features.operators.Operators.calculateSum
import com.dot.gonit.search.SearchManager.doSearch
import com.dot.gonit.search.SearchManager.setKeyword
import com.google.android.material.snackbar.Snackbar
import com.nightonke.boommenu.BoomButtons.HamButton
import com.zaphlabs.filechooser.KnotFileChooser
import com.zaphlabs.filechooser.Sorter
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var viewList = mutableListOf<View>()
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var preferences: SharedPreferences
    private val titles = arrayOf(R.string.doc, R.string.policy, R.string.save, R.string.clear)
    private val columns = arrayOf("Expression", "Result")
    private val subTitles =
        arrayOf(R.string.sub_doc, R.string.sub_policy, R.string.sub_save, R.string.sub_clear)
    private val icons =
        arrayOf(R.drawable.ic_doc, R.drawable.ic__policy, R.drawable.ic_save, R.drawable.ic_delete)
    private val startExpressions = arrayOf(
        "// This is how Gonit work",
        "// Please press '->' icon to goto new line",
        " ",
        "1 + 3 * (5 + 5) - 4 / 2",
        "10 plus 4 minus 3 mul 2",
        "10% of 100",
        "1024*(MB) in (GB)"
    )

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        preferences = getSharedPreferences("com.dot.gonit", MODE_PRIVATE)
        initBoomButton()

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext)))
        ).get(MainActivityViewModel::class.java)

        binding.editor.editorET.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding.editor.editorET.addTextWatcher(binding.editor.resultTV, 1)
        binding.editor.editorET.addOnEditorActionListener()
        binding.editor.resultTV.setOnLongPressListener()
        binding.editor.lineTV.text = "1."

        if (preferences.getBoolean("firststart", true)) {
            preferences.edit().putBoolean("firststart", false).apply()
            showStartExpressions()
        } else {
            retrieve()
        }
        showKeyboard()
        hashFromSHA1("E1:C8:91:43:74:86:7C:60:D3:3E:77:22:3D:33:10:DB:6F:0F:1B:A9")
    }

    fun hashFromSHA1(sha1: String) {
        val arr = sha1.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val byteArr = ByteArray(arr.size)

        for (i in arr.indices) {
            byteArr[i] = Integer.decode("0x" + arr[i])!!.toByte()
        }

        Log.e("hasan : ", Base64.encodeToString(byteArr, Base64.NO_WRAP))
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun addNewLine(anyText: String?) {
        val inflater: LayoutInflater =
            baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val newView = inflater.inflate(R.layout.editor, null)
        val textView: TextView = newView.findViewById(R.id.resultTV)
        val editText: EditText = newView.findViewById(R.id.editorET) as EditText

        textView.setOnLongPressListener()

        editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editText.addOnEditorActionListener()
        editText.requestFocus()

        editText.addTextWatcher(textView, viewList.size + 2)
        editText.addOnKeyListener(newView, viewList.size + 2)

        anyText?.let {
            editText.setText(it)
            editText.setSelection(editText.text.length)
            editText.requestFocus()
        }
        binding.container.addView(newView)
        viewList.add(newView)
        setLineNumber()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun EditText.addTextWatcher(textView: TextView, id: Int) {
        val query = MutableStateFlow("")
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let { query.value = s.toString().trim() }
            }
        })

        lifecycleScope.launch {
            query.debounce(500)
                .distinctUntilChanged()
                .flowOn(Default)
                .collect {
                    setKeyword(this@addTextWatcher.editableText)
                    withContext(Default) {
                        showResult(textView, this@addTextWatcher.editableText, doSearch(it))
                    }
                    withContext(Main) {
                        insert(id, it)
                    }
                }
        }
    }

    private fun EditText.addOnKeyListener(parent: View, id: Int) {
        this.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (this.text.isEmpty()) {
                    (parent.parent as LinearLayout).removeView(parent)
                    if (viewList.isNotEmpty()) {
                        val index = viewList.indexOf(parent) - 1
                        if (index > -1) {
                            val editText: EditText =
                                viewList[index].findViewById(R.id.editorET) as EditText
                            editText.requestFocus()
                        } else {
                            binding.editor.editorET.setSelection(binding.editor.editorET.text.toString().length)
                            binding.editor.editorET.requestFocus()
                        }
                    }
                    viewList.remove(parent)
                    setLineNumber()
                    delete(id)
                }
            }
            false
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun EditText.addOnEditorActionListener() {
        this.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                handled = true
                addNewLine(null)
            }
            handled
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun retrieve() {
        viewModel.retrieve().observe(this, Observer {
            it?.let {
                if (it.status == Status.SUCCESS) {
                    it.data?.let { data ->
                        if (data.isNotEmpty()) {
                            deleteAll(data)
                        } else {
                            binding.editor.editorET.requestFocus()
                        }
                    }
                }
            }
        })
    }


    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun deleteAll(data: List<Editor>) {
        viewModel.delete().observe(this, {
            it?.let {
                if (it.status == Status.SUCCESS) {
                    for ((index, value) in data.withIndex()) {
                        if (index == 0) {
                            binding.editor.editorET.setText(data.first().text)
                            binding.editor.editorET.setSelection(binding.editor.editorET.text.toString().length)
                            binding.editor.editorET.requestFocus()
                        } else {
                            addNewLine(value.text)
                        }
                    }
                }
            }
        })
    }

    private fun TextView.setOnLongPressListener() {
        this.setOnLongClickListener {
            copyText(this.text.toString())
            true
        }
    }

    private fun setLineNumber() {
        for ((index, value) in viewList.withIndex()) {
            val line: TextView = value.findViewById(R.id.lineTV)
            line.text = (index + 2).toString() + "."
        }
    }

    private fun initBoomButton() {
        for (i in 0 until binding.bmb.piecePlaceEnum.pieceNumber()) {
            val builder = HamButton.Builder()
                .normalImageRes(icons[i])
                .normalTextRes(titles[i])
                .typeface(Typeface.SANS_SERIF)
                .subNormalTextRes(subTitles[i])
                .subTypeface(Typeface.SANS_SERIF)
                .listener {
                    when (it) {
                        0 -> {
                            gotoMarkDownViewPage()
                        }
                        1 -> {
                            gotoWebViewPage()
                        }
                        2 -> {
                            saveFile()
                        }
                        3 -> {
                            clearEditor()
                        }
                    }
                }
            binding.bmb.addBuilder(builder)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseDirectory()
            }
        }
    }

    private fun saveFile() {
        CoroutineScope(IO).launch {
            delay(1200L)
            withContext(Main) {
                if (isPermissionGranted()) {
                    chooseDirectory()
                } else {
                    requestPermission()
                }
            }
        }
    }

    private fun chooseDirectory() {
        KnotFileChooser(
            this@MainActivity,
            allowBrowsing = true,
            allowCreateFolder = true,
            allowMultipleFiles = false,
            allowSelectFolder = false,
            minSelectedFiles = 0,
            maxSelectedFiles = 0,
            showFiles = false,
            showFoldersFirst = true,
            showFolders = true,
            showHiddenFiles = false,
            initialFolder = Environment.getExternalStorageDirectory(),
            restoreFolder = false,
            cancelable = true,
            fileType = KnotFileChooser.FileType.ALL
        )
            .title(R.string.select_folder_to_save)
            .sorter(Sorter.ByNewestModification)
            .onSelectedFileUriListener {
                if (it.isNotEmpty()) {
                    Snackbar.make(binding.root, R.string.please_wait, Snackbar.LENGTH_INDEFINITE)
                        .show()
                    saveFile(it.first())
                }
            }
            .show()
    }

    private fun saveFile(uri: Uri) {
        val workbook = HSSFWorkbook() as Workbook
        val sheet = workbook.createSheet("Gonit")
        val headerFront = workbook.createFont() as org.apache.poi.ss.usermodel.Font
        headerFront.fontHeightInPoints = 12
        headerFront.color = IndexedColors.GREEN.index
        val headerCellStyle = workbook.createCellStyle()
        headerCellStyle.setFont(headerFront)
        val headerRow = sheet.createRow(0)
        for ((index, value) in columns.withIndex()) {
            val cell = headerRow.createCell(index)
            cell.setCellValue(value)
            cell.cellStyle = headerCellStyle
        }
        val firstRow = sheet.createRow(1)
        firstRow.createCell(0).setCellValue(binding.editor.editorET.text.toString())
        firstRow.createCell(1).setCellValue(binding.editor.resultTV.text.toString())
        for ((index, value) in viewList.withIndex()) {
            val textView: TextView = value.findViewById(R.id.resultTV)
            val editText: EditText = value.findViewById(R.id.editorET) as EditText
            val row = sheet.createRow(index + 2)
            row.createCell(0).setCellValue(editText.text.toString())
            row.createCell(1).setCellValue(textView.text.toString())
        }
        try {
            val fileOutputStream = FileOutputStream(File(uri.path + "/Gonit_Document.xlsx"))
            workbook.write(fileOutputStream)
            fileOutputStream.close()
            Snackbar.make(binding.root, R.string.save_successful, Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Snackbar.make(binding.root, R.string.save_failed, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun gotoWebViewPage() {
        CoroutineScope(IO).launch {
            delay(1200L)
            Intent(this@MainActivity, WebViewActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun gotoMarkDownViewPage() {
        CoroutineScope(IO).launch {
            delay(1200L)
            Intent(this@MainActivity, MarkDownViewActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun clearEditor() {
        viewModel.delete().observe(this, {
            it?.let {
                if (it.status == Status.SUCCESS) {
                    viewList.forEach { parent ->
                        (parent.parent as LinearLayout).removeView(parent)
                    }
                    viewList.clear()
                    binding.editor.editorET.setText("")
                    binding.editor.editorET.requestFocus()
                }
            }
        })
    }


    private fun insert(id: Int, text: String) {
        viewModel.insert(Editor(id, text)).observe(this, {})
    }

    private fun delete(id: Int) {
        viewModel.delete(id).observe(this, {})
    }

    private fun showKeyboard() {
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun showResult(textView: TextView, editable: Editable?, result: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            if (result == null) {
                textView.text = ""
            } else {
                when (result) {
                    "//" -> {
                        textView.text = ""
                        editable?.setSpan(
                            ForegroundColorSpan(Color.parseColor("#808080")),
                            0,
                            editable.toString().trim().length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    "avg" -> {
                        calculateAvg(
                            editable.toString().trim(),
                            textView,
                            binding.editor.resultTV,
                            viewList
                        )
                    }
                    "sum" -> {
                        calculateSum(
                            editable.toString().trim(),
                            textView,
                            binding.editor.resultTV,
                            viewList
                        )
                    }
                    else -> {
                        textView.text = result
                    }
                }
            }
        }
    }

    private fun copyText(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Result", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun showStartExpressions() {
        for ((index, value) in startExpressions.withIndex()) {
            if (index == 0) {
                binding.editor.editorET.setText(value)
            } else {
                addNewLine(value)
            }
        }
    }


    companion object {
        private const val TAG = "hasan"
    }


}
