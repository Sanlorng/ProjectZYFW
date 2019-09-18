package com.bigcreate.library

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log

fun Context.getPath(uri: Uri):String?{
    var path:String?
    if (ContentResolver.SCHEME_CONTENT == (uri.scheme)) {
        if (DocumentsContract.isDocumentUri(this, uri)) {
            if (uri.isExternalStorageDocument()) {
                // ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                if ("primary" == (type)) {
                    path = Environment.getExternalStorageDirectory().path + "/" + split[1]
                    return path
                }
            } else if (uri.isDownloadsDocument()) {
                // DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri);
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        id.toLong())
                path = getDataColumn(contentUri, null, null)
                return path
            } else if (uri.isMediaDocument()) {
                // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                var contentUri: Uri? = null
                when {
                    "image" == type -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" == type -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" == type -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                var selection = "_id=?"
                var selectionArgs = arrayOf(split[1])
                path = getDataColumn(contentUri!!, selection, selectionArgs)
                return path
            }
        }
    }

    return null


}
private fun Context.getDataColumn(uri: Uri, selection:String?, selectionArgs:Array<String>?):String? {
    var cursor : Cursor? = null
    val column = "_data"
    val  projection = arrayOf(column)
    try {
        cursor = this.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(columnIndex)
        }
    } finally {
        cursor?.close()
    }
    return null
}
private fun Uri.isExternalStorageDocument():Boolean {
    return "com.android.externalstorage.documents" == this.authority
}

private fun Uri.isDownloadsDocument():Boolean {
    return "com.android.providers.downloads.documents" == this.authority
}

private fun Uri.isMediaDocument():Boolean {
    return "com.android.providers.media.documents" == this.authority
}

private fun Uri.isFileExplorer(): Boolean {
    //content://com.android.fileexplorer.myprovider/external_files/DCIM/1549763195593.jpg
    return "com.android.fileexplorer.myprovider" == this.authority
}

private fun Uri.isMiuiGallery():Boolean {
    //content://com.miui.gallery.open/raw//storage/emulated/0/DCIM/Camera/IMG_20190301_154642.jpg
    return "com.miui.gallery.open" == authority
}
fun String.logIt(tag:String) = Log.d(tag,this)