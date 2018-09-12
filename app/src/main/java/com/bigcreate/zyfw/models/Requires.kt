package com.bigcreate.zyfw.models

data class LoginRequire(var ip_address: String?, var username: String?, var password: String?, var token: String?)
data class RegisterRequire(var ip_address: String?, var username: String?, var password: String?)
data class InfoRequire(var username: String, var user_nick: String, var user_sex: String,
                       var user_identity: String, var user_address: String, var user_phone: String)
data class UpdateInfoRequire(var username: String, var user_address: String, var user_phone: String)
data class SearchRequire(var project_region: String?, var project_topic: String?)
data class ReleaseProjectRequire(var project_topic: String, var project_content: String, var project_region: String,
                          var project_address: String, var procject_principal_name: String, var project_people_numbers : String,
                          var username: String, var videoBase64: String, var pictureBase64: String)
