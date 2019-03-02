package com.bigcreate.zyfw.callback

interface FillTextCallBack {
    fun getTextContent(): CharSequence
    fun setTextContent(content: CharSequence)
    fun getProjectId(): String
}