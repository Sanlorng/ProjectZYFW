package com.bigcreate.zyfw.models

data class LoginResponse(var data: String, var stateCode: String, var token: String?)
data class RegisterResponse(var data: String, var stateCode: String, var token: String?)
data class InfoResponse(var data: String, var stateCode: Number)
data class ProjectResponse(var data: String, var project_id: Number, var stateCode: String)
data class SearchResponse(var data: String,var stateCode: String, var content:List<Project>)
data class CommentResponse(var data: String, var stateCode: String,val content: List<Comment>)