package com.bigcreate.library

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import android.provider.MediaStore
import android.provider.DocumentsContract




fun Bitmap.toBase64():String? {

    var result:String ?= null
     var baos:ByteArrayOutputStream?= null
    try {

            baos = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        baos.run {
            flush()
            close()
        }

        val bitmapBytes = baos.toByteArray()
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            baos?.run {
                flush()
                close()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return result
}


fun String.toBitmap():Bitmap {
    val bytes = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun File.toBase64():String?{
    val inputFile = FileInputStream(this)
    val buffer = ByteArray(this.length().toInt())
    inputFile.read(buffer)
    inputFile.close()
    return Base64.encodeToString(buffer,Base64.DEFAULT)
}

fun Context.getRealPathFromURI_API19(uri: Uri): String {
    var filePath = ""
    val wholeID = DocumentsContract.getDocumentId(uri)

    // Split at colon, use second item in the array
    val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

    val column = arrayOf(MediaStore.Images.Media.DATA)

    // where id is equal to
    val sel = MediaStore.Images.Media._ID + "=?"

    val cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null)
    val columnIndex = cursor.getColumnIndex(column[0])

    if (cursor.moveToFirst()) {
        filePath = cursor.getString(columnIndex)
    }
    cursor.close()
    return filePath
}
@Throws(IOException::class)
fun Context.getBitmapFromUri(uri: Uri): Bitmap {
    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
    val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor.close()
    return image
}