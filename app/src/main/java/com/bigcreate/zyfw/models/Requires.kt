package com.bigcreate.zyfw.models

data class LoginRequire(var ipAddress: String?, var username: String?, var password: String?, var token: String?)
data class RegisterRequire(var ipAddress: String?, var username: String?, var password: String?)
data class InfoRequire(var username: String, var userNick: String, var userSex: String,
                       var userIdentify: String, var userAddress: String, var userPhone: String, var imgBase64: String)
data class UpdateInfoRequire(var username: String, var userAddress: String, var userPhone: String)
data class SearchRequire(var projectId: String?, var projectRegion: String?, var projectTopic: String?)
data class ReleaseProjectRequire(var projectTopic: String, var projectContent: String, var projectRegion: String,
                          var projectAddress: String,var latitude: Double, var longitude: Double, var projectPrincipalName: String,var projectPrincipalPhone: String, var projectPeopleNumbers : String,
                          var username: String, var videoBase64: String, var pictureBase64: String, var projectUserId: String, var projectTypeId: String)
data class GetInfoRequire(var username: String)