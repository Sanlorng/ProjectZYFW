package com.bigcreate.library

import android.content.Context
import android.graphics.*
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
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)

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

    this.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null)?.run {
    val columnIndex = getColumnIndex(column[0])

    if (moveToFirst()) {
        filePath = getString(columnIndex)
    }
    close()
    }
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

val Bitmap.roundBitmap:Bitmap
get(){
    var width = this.width
    var height = this.height
    val roundPx: Float
    val left: Float
    val top: Float
    val right: Float
    val bottom: Float
    val dst_left: Float
    val dst_top: Float
    val dst_right: Float
    val dst_bottom: Float
    if (width <= height) {
        roundPx = (width / 2).toFloat()
        top = 0f
        bottom = width.toFloat()
        left = 0f
        right = width.toFloat()
        height = width
        dst_left = 0f
        dst_top = 0f
        dst_right = width.toFloat()
        dst_bottom = width.toFloat()
    } else {
        roundPx = (height / 2).toFloat()
        val clip = ((width - height) / 2).toFloat()
        left = clip
        right = width - clip
        top = 0f
        bottom = height.toFloat()
        width = height
        dst_left = 0f
        dst_top = 0f
        dst_right = height.toFloat()
        dst_bottom = height.toFloat()
    }
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val color = -0xbdbdbe
    val paint = Paint()
    val src = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    val dst = Rect(dst_left.toInt(), dst_top.toInt(), dst_right.toInt(), dst_bottom.toInt())
    val rectF = RectF(dst)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color =color
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, src, dst, paint)
    return output

}