package com.bigcreate.zyfw.models

data class LoginResponse(var data: String, var stateCode: String, var token: String?, var content: String?)
data class RegisterResponse(var data: String, var stateCode: String, var token: String?)
data class InfoResponse(var data: String, var stateCode: String)
data class SearchResponse(var data: String,var stateCode: String, var content:List<Project>)
data class ProjectResponse(var data: String,var stateCode: String, var content:Project)
data class CommentResponse(var data: String, var stateCode: String,val content: List<Comment>)
data class BaseResponse(var data: String, var stateCode: String)
data class ReleaseResponse(var data: String,var stateCode: String, var projectId: Int)
data class UserInfoResponse(var data: String, var stateCode: String, var content: UserInfo)