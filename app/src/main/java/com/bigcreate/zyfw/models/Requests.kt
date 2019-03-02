package com.bigcreate.zyfw.models

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import java.io.File

data class SimpleRequest(var token: String, var username: String)
data class LoginRequest(var username: String, var password: String)
data class IsSetupInfoRequest(var token: String, var username: String)
data class RegisterRequest(var username: String, var password: String, var code: String)
data class InitPersonInfoRequest(var username: String, var userNick: String, var userSexCode: Int,
                                 var userIdentifyCode: Int, var userAddress: String, var userPhone: String, var token: String)

data class UpdateInfoRequest(var username: String, var userAddress: String, var userPhone: String)
data class SearchRequest(var token: String, var projectRegion: String?, var projectTopic: String?, var projectContent: String?)
data class CreateProjectRequest(var projectTopic: String, var projectContent: String, var projectRegion: String,
                                var projectAddress: String, var latitude: Double, var longitude: Double, var projectPrincipalName: String, var projectPrincipalPhone: String, var projectPeopleNumbers: String,
                                var username: String, var token: String, var projectTypeId: Int)

data class GetProjectRequest(var token: String, var projectId: String)
data class UpdateProjectRequest(var projectTopic: String, var projectContent: String, var projectAddress: String, var projectPrincipalName: String, var projectRegion: String,
                                var projectPrincipalPhone: String, var projectPeopleNumbers: String, var username: String, var projectId: String, var token: String)

data class CreateCommentRequest(
        val comment: String,
        val projectId: String,
        val token: String,
        val username: String
)

data class CommentListRequest(var token: String, var projectId: String, var pageNum: Int)
data class ProjectUnFavoriteRequest(var projectId: Int, var username: String, var token: String)
data class ProjectFavoriteRequest(var projectId: Int, var username: String, var token: String, var projectClassifyId: String)
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

data class RestResult<T>(val message: String, val code: Int) {
    val data: T? = null
}

class FileUploadRequest(file: File, token: String, username: String) {
    var part: MultipartBody.Part
    var token: RequestBody
    var username: RequestBody

    init {
        val type = MediaType.parse("multipart/form-data")
        part = MultipartBody.Part.createFormData("file", file.name, RequestBody.create(type, file))
        this.token = RequestBody.create(type, token)
        this.username = RequestBody.create(type, username)
    }
}

class FilesUploadRequest(files: List<File>, token: String, username: String, projectId: String) {
    var parts = ArrayList<MultipartBody.Part>()
//    var part : MultipartBody
    var token: RequestBody
    var username: RequestBody
    var projectId: RequestBody
    init {

        val type = MediaType.parse("multipart/form-data")
//        val builder = MultipartBody.Builder()
        files.forEach {
            parts.add(MultipartBody.Part.createFormData("file", it.name, RequestBody.create(type, it)))
//            builder.addFormDataPart("file",it.name, RequestBody.create(type,it))
        }
//        part = builder.build()
//        part = MultipartBody.Part.createFormData("file",file.name, RequestBody.create(type, file))
        this.token = RequestBody.create(type, token)
        this.username = RequestBody.create(type, username)
        this.projectId = RequestBody.create(type,projectId)
    }
}