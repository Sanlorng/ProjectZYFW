package com.bigcreate.zyfw.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.SpinnerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.MediaFolderAdapter
import com.bigcreate.zyfw.adapter.PickerAdapter
import com.bigcreate.zyfw.viewmodel.MediaViewModel
import kotlinx.android.synthetic.main.activity_media_picker.*

class MediaPickerActivity : AppCompatActivity() {
    private lateinit var viewModel : MediaViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_picker)
        viewModel = ViewModelProvider(this)[MediaViewModel::class.java]
        viewModel.listFolder.observe(this, Observer {
            folderList.adapter = MediaFolderAdapter(this,it)
            folderList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    listMedia.adapter = PickerAdapter(it[id.toInt()].list) { uri: String, view: View, position: Int, isChecked: Boolean ->
                        toolbarMediaPicker.menu.findItem(R.id.selectDoneCity).title = SpannableString("确定(${listChecked.size()})")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    listMedia.adapter = null
                }
            }
        })
        viewModel.getImages()
    }
}
