package com.bigcreate.zyfw.models

data class SearchResponse(var message: String, var stateCode: Int, var content: List<Project>)

data class Data(
        val content: String,
        val newToken: String
)