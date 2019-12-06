package com.bigcreate.zyfw.viewmodel

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MediaViewModel(application: Application):AndroidViewModel(application) {
    private val projectionImages = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )
    private val projectionVideos = arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
    )
    private val map = SparseArray<MediaList>()
    val listMedias = MutableLiveData<List<Uri>>()
    val listFolder = MutableLiveData<SparseArray<MediaList>>()
    fun getImages(folder: String = "") {
        viewModelScope.launch {
            if (listFolder.value == null) {
                initImages()
            }

        }
    }

    fun getVideos(folder: String = "") {
        viewModelScope.launch {
            if (listFolder.value == null) {
                initVideos()
            }
        }
    }

    private fun initImages() {
        map.clear()
        getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionImages,
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " desc"
        )?.apply {
            moveToFirst()
            do {
                val path = getString(getColumnIndex(MediaStore.Images.Media.DATA))
                val bucketId = getInt(getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                if (map[bucketId] != null) {
                    map[bucketId].list.add(path)
                }else {
                    map[bucketId] = MediaList(getString(getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),bucketId)
                    map[bucketId].list.add(path)
                }
            } while (moveToNext())
            listFolder.postValue(map)
            close()
        }
    }

    private fun initVideos() {

    }

    data class MediaList(
            val bucketName:String = "",
            val bucketId: Int = -1,
            val list:ArrayList<String> = ArrayList()
    )
}