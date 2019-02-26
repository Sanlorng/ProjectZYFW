package com.bigcreate.zyfw.fragments

interface FillTextCallBack {
    fun getTextContent(): CharSequence
    fun setTextContent(content: CharSequence)
    fun getProjectId(): String
}