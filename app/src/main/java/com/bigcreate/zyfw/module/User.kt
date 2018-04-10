package com.bigcreate.zyfw.module

import org.litepal.crud.DataSupport

/**
 * Create by Sanlorng on 2018/4/9
 */
data class User(val name: String, val  password: String): DataSupport(){
    var isLoginUser : Int ?= null
}