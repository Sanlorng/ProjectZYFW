package com.bigcreate.zyfw.mvp.base

data class SearchModel(val projectId: Int, val projectTopic: String, val projectPeopleNumbers: String, val projectAddress: String)
data class LoginModel(val username: String, var password: String, var token: String)