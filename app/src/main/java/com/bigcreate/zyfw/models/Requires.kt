package com.bigcreate.zyfw.models

data class LoginRequest(var username: String, var password: String)
data class IsSetupInfoRequest(var token: String, var username: String)
data class RegisterRequest(var username: String, var password: String, var code: String )
data class InitPersonInfoRequest(var username: String, var userNick: String, var userSexCode: Int,
                       var userIdentifyCode: Int, var userAddress: String, var userPhone: String, var token: String)
data class UpdateInfoRequire(var username: String, var userAddress: String, var userPhone: String)
data class SearchRequest(var token: String, var projectRegion: String?, var projectTopic: String?, var projectContent: String?)
data class CreateProjectRequest(var projectTopic: String, var projectContent: String, var projectRegion: String,
                          var projectAddress: String,var latitude: Double, var longitude: Double, var projectPrincipalName: String,var projectPrincipalPhone: String, var projectPeopleNumbers : String,
                          var username: String, var token: String, var projectTypeId: Int)
data class GetInfoRequire(var username: String)

data class UpdateInfo(

        var updateId: Int? = 1,

        val packageName: String = "",

        val versionCode: Int = 0,

        var versionName: String = "",

        var changelog: String = "",

        var path: String = "",

        var minSDK: Int = 0,

        var targetSDK: Int = 0,

        var label: String = ""
)

data class RestResult<T>(val message: String, val code: Int){
        val data: T? = null
}