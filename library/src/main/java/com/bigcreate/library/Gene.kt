package com.bigcreate.library

import com.google.gson.Gson
import java.lang.reflect.Field

fun Any.fieldStrings():String{
    var string = ""
    javaClass.declaredFields.forEach {

    }
    return string
}

fun Any.toJson():String{
    return Gson().toJson(this)
}