package com.bigcreate.zyfw.models

data class BaseResponse(var data: String, var stateCode: Int)
data class getPhoneCodeResponse(var message: String, var stateCode: Int, var data: GetPhoneCodeData)
data class LoginByPassResponse(var message: String, var stateCode: Int, var token: String?, var content: String?)
data class RegisterResponse(var message: String, var stateCode: Int, var token: String?)
data class InfoResponse(var message: String, var stateCode: Int)
data class SearchResponse(var message: String,var stateCode: Int, var content:List<Project>)
data class ProjectResponse(var message: String,var stateCode: Int, var content:Project)
data class CommentResponse(var message: String, var stateCode: Int,val content: List<Comment>?)
data class ReleaseResponse(var message: String,var stateCode: Int, var projectId: Int)
data class UserInfoResponse(var message: String, var stateCode: Int, var content: UserInfo)
data class ContentResponse(var message: String, var stateCode: Int, var content: String?)