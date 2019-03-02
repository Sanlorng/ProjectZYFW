package com.bigcreate.library

import com.google.gson.Gson
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*

fun Any.fieldStrings():String{
    var string = ""
    javaClass.declaredFields.forEach {

    }
    return string
}

fun Any.toJson():String{
    return Gson().toJson(this)
}

val Boolean?.valueOrNotNull:Boolean
get() {
    return this ?: false
}

fun String.transDate(old:String,new:String):String {
    return SimpleDateFormat(new, Locale.CHINA).format(SimpleDateFormat(old,Locale.CHINA).parse(this))
}