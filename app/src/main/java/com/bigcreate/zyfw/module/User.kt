package com.bigcreate.zyfw.module

import org.litepal.crud.DataSupport
import java.util.*

/**
 * Create by Sanlorng on 2018/4/9
 */
data class User(val name: String, val  password: String): DataSupport(){
    var isLogin : Boolean ?= null
    var registTime: Date ?= null
}