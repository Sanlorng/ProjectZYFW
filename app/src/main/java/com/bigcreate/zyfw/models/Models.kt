package com.bigcreate.zyfw.models

data class SearchModel(val projectId: Int, val projectTopic: String, val projectPeopleNumbers: String, val projectAddress: String)
data class LoginModel(val username: String, var password: String, var token: String)