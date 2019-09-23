package com.bigcreate.zyfw.models

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

data class CrashLog(
        val versionName: String,
        val packageName: String,
        val versionCode: String,
        val crashString: String
)
data class ExploreRequest(var token: String, var dyId: Int)
data class ExploreEditRequest(var token: String, var dyId: Int,var dyContent: String)
data class ExploreDeleteRequest(var dyId: Int,var dyReleaseUserId: Int, var token: String)
data class ExploreCommentInfoRequest(var token: String,var dyCommentId:Int, var pageNum: Int)
data class ExploreItemFavoriteRequest(var token:String, val dyCollectionId: Int)
data class ExploreItemLikeRequest(var token:String, val dyPraiseId: Int)
data class ExploreCommentRequest(var token: String,val dyCommentId: Int, val dyCommentContent: String)
data class ExploreCommentDeleteRequest(var token: String,var dyCommentId: Int,var dyCommentUserId: Int, var dyCommentTime: String)
data class TokenRequest(var token: String)
data class PageRequest(var token: String, var pageNum: Int)
data class SimpleRequest(var token: String, var userId: Int)
data class SimplePageRequest(var token: String, var userId: Int, var pageNum: Int)
data class LoginRequest(var username: String, var password: String)
data class IsSetupInfoRequest(var token: String, var username: String)
data class RegisterRequest(var username: String, var password: String, var idNumber: String,var realName:String, var code: String)
data class InitPersonInfoRequest(var username: String, var userNick: String, var userSexCode: Int,
                                 var userIdentifyCode: Int, var userAddress: String, var userPhone: String, var token: String, var userId: Int)

data class UpdateInfoRequest(var userAddress: String, var userPhone: String,val userId: Int,val token: String)
data class SearchRequest(var token: String, var projectRegion: String?, var projectTopic: String?, var projectContent: String?, var pageNum: Int)
data class CreateProjectRequest(var projectTopic: String, var projectContent: String, var projectRegion: String,
                                var projectAddress: String, var latitude: Double, var longitude: Double, var projectPrincipalName: String, var projectPrincipalPhone: String, var projectPeopleNumbers: String,
                                var username: String, var token: String, var projectTypeId: Int)

data class GetProjectRequest(var token: String, var projectId: Int)
data class UpdateProjectRequest(var projectTopic: String, var projectContent: String, var projectAddress: String, var projectPrincipalName: String, var projectRegion: String,
                                var projectPrincipalPhone: String, var projectPeopleNumbers: String, var username: String, var projectId: String, var token: String)

data class CreateCommentRequest(
        val comment: String,
        val projectId: String,
        val token: String,
        val userId: Int
)

data class CommentListRequest(var token: String, var projectId: String, var pageNum: Int)
data class ProjectFavoriteRequest(var projectId: Int, var projectUserId: Int, var token: String, var projectClassifyId: String)
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

data class RestResult<T>(val message: String, val code: Int, val data: T)

class FileUploadRequest(file: File, token: String, userId: Int) {
    var part: MultipartBody.Part
    var token: RequestBody
    var username: RequestBody

    init {
        val type = MediaType.parse("multipart/form-data")
        part = MultipartBody.Part.createFormData("file", file.name, RequestBody.create(type, file))
        this.token = RequestBody.create(type, token)
        this.username = RequestBody.create(type, userId.toString())
    }
}

class PublishExploreRequest(files: List<File>, token: String, dyContent: String) {
    var parts = ArrayList<MultipartBody.Part>()
    //    var part : MultipartBody
    var token: RequestBody
    var dyContent: RequestBody

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
        this.dyContent = RequestBody.create(type, dyContent)
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
        this.projectId = RequestBody.create(type, projectId)
    }
}