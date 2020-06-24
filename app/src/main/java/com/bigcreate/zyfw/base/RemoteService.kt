package com.bigcreate.zyfw.base

import android.util.Log
import com.bigcreate.zyfw.models.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RemoteService {

    //获取手机验证码
    @GET("getPhoneNumber")
    fun getPhoneCode(@Query("username") phoneNumber: String): Call<JsonObject>

    //用户注册
    @POST("register")
    fun register(@Body registerRequire: RegisterRequest): Call<JsonObject>

    //账号密码登录
    @POST("loginByPass")
    fun loginByPass(@Body loginRequire: LoginRequest): Call<JsonObject>

    //重置密码
    @POST("resetPassword")
    fun resetPassword(@Body registerRequest: RegisterRequest): Call<JsonObject>

    //初始化个人信息
    @POST("insertInfo")
    fun initPersonInfo(@Body initPersonInfoRequest: InitPersonInfoRequest): Call<JsonObject>

    //用户设置头像
    @Multipart
    @POST("upLoadHeadPicture")
    fun setupUserAvatar(@Part("token") token: RequestBody, @Part("userId") username: RequestBody,@Part file: MultipartBody.Part ): Call<JsonObject>

    //修改个人信息
    @POST("updateInfo")
    fun updatePersonInfo(@Body updateInfoRequest: UpdateInfoRequest): Call<JsonObject>

    //查询用户详情(本人)
    @POST("getUserInfoById")
    fun getUserInfoBySelf(@Body simpleRequest: SimpleRequest): Call<JsonObject>

    //查询用户头像
    @POST("downLoadHeadPicture")
    @Streaming
    fun getUserAvatar(@Body simpleRequest: SimpleRequest): Call<ResponseBody>

    //项目发布
    @POST("projectissue")
    fun createProject(@Body createProjectRequest: CreateProjectRequest): Call<JsonObject>

    //添加项目图片
    @Multipart
    @POST("addPicture")
    fun addProjectPicture(@Part files: ArrayList<MultipartBody.Part>, @Part("token") token: RequestBody,
                          @Part("username") username: RequestBody,
                          @Part("projectId") projectId: RequestBody): Call<JsonObject>

    //添加项目视频
    @Multipart
    @POST("addVideo")
    fun addProjectVideo(@Part files: ArrayList<MultipartBody.Part>, @Part("token") token: RequestBody,
                        @Part("username") username: RequestBody,
                        @Part("projectId") projectId: RequestBody): Call<JsonObject>

    //修改已发布项目的部分信息
    @POST("updateIssue")
    fun updateProject(@Body updateProjectRequest: UpdateProjectRequest): Call<JsonObject>

    //删除已发布项目
    @POST("deleteIssueInfo")
    fun deleteProject(@Body getProjectRequest: GetProjectRequest): Call<JsonObject>

    //获取用户发布的项目
    @POST("getIssueInfoByUser")
    fun getUserReleasedList(@Body pageRequest: PageRequest): Call<JsonObject>

    //获取他人发布的项目
    @POST("getIssueInfoByUserId")
    fun getOtherUserReleasedList(@Body simplePageRequest: SimplePageRequest): Call<JsonObject>

    //获取他人参加的项目
    @POST("getJoinProInfoByStuUserId")
    fun getOtherUserJoinedList(@Body simplePageRequest: SimplePageRequest): Call<JsonObject>

    //查询项目详情
    @POST("projectByInfo")
    fun getProjectInfo(@Body getProjectRequest: GetProjectRequest): Call<JsonObject>

    //收藏项目
    @POST("Collection")
    fun favoriteProject(@Body favoriteRequest: ProjectFavoriteRequest): Call<JsonObject>

    //取消收藏项目
    @POST("RemoveCollection")
    fun unFavoriteProject(@Body favoriteRequest: ProjectFavoriteRequest): Call<JsonObject>

    //模糊搜索
    @POST("SelectIssueByBlur")
    fun searchProjectByBlur(@Body searchRequest: SearchRequest): Call<JsonObject>

    //添加项目评论
    @POST("issueComment")
    fun createProjectComment(@Body createCommentRequest: CreateCommentRequest): Call<JsonObject>

    //查看项目评论
    @POST("selectIssueComment")
    fun getProjectComment(@Body commentListRequest: CommentListRequest): Call<JsonObject>

    //获取推荐项目
    @POST("getRecData")
    fun getRecommendData(@Body simpleRequest: SimpleRequest): Call<JsonObject>

    //获取轮播图数据
    @GET("getCountAndPicture")
    fun getCountAndPicture(): Call<RestResult<CountAndPictureModel>>

    //获取收藏的项目
    @POST("getColletionInfos")
    fun getUserFavoriteList(@Body pageRequest: PageRequest): Call<JsonObject>

    //加入项目
    @POST("stuJoinProject")
    fun joinProject(@Body getProjectRequest: GetProjectRequest): Call<JsonObject>

    //获取学生加入的项目
    @POST("getJoinProInfoByStu")
    fun getUserJoinedList(@Body pageRequest: PageRequest): Call<JsonObject>

    //获取已加入项目的学生
    @POST("getJoinNums")
    fun getProjectJoinedMember(@Body projectRequest: GetProjectRequest): Call<JsonObject>

    //发布动态
    @Multipart
    @POST("dynamicPublish")
    fun explorePublish(@Part files: ArrayList<MultipartBody.Part>, @Part("token") token: RequestBody,
                       @Part("dyContent") dyContent: RequestBody): Call<JsonObject>

    //获取动态信息流
    @POST("getDynamicInfo")
    fun getExploreList(@Body pageRequest: PageRequest): Call<JsonObject>

    //获取动态详情
    @POST("dynamicInfo")
    fun getExploreDetails(@Body exploreRequest: ExploreRequest):Call<JsonObject>

    //更新动态内容
    @POST("updateDynamicInfo")
    fun updateExploreItem(@Body exploreEditRequest: ExploreEditRequest):Call<JsonObject>

    //删除动态
    @POST("delDynamicInfo")
    fun delExploreItem(@Body deleteRequest: ExploreDeleteRequest): Call<JsonObject>

    //获取动态的评论
    @POST("getDynamicCommentInfo")
    fun getExploreComment(@Body exploreCommentInfoRequest: ExploreCommentInfoRequest): Call<JsonObject>

    //评论动态
    @POST("commentDynamicInfo")
    fun commentExploreItem(@Body exploreCommentRequest: ExploreCommentRequest): Call<JsonObject>

    //删除评论
    @POST("delCommentDynamicInfos")
    fun delExploreComment(@Body exploreCommentDeleteRequest: ExploreCommentDeleteRequest): Call<JsonObject>

    //动态点赞
    @POST("dynamicPraise")
    fun exploreItemLike(@Body exploreItemLikeRequest: ExploreItemLikeRequest): Call<JsonObject>

    //动态取消赞
    @POST("cancelDynamicPraise")
    fun exploreItemUnlike(@Body exploreItemLikeRequest: ExploreItemLikeRequest): Call<JsonObject>

    //收藏动态
    @POST("collectionDynamicInfo")
    fun exploreItemFavorite(@Body exploreItemFavoriteRequest: ExploreItemFavoriteRequest): Call<JsonObject>

    //取消收藏动态
    @POST("delCollectionDynamicInfo")
    fun exploreItemUnfavorite(@Body exploreItemFavoriteRequest: ExploreItemFavoriteRequest): Call<JsonObject>

    //获取昵称和头像
    @GET("getHeadLinkAndNick")
    fun getHeadLinkAndNick(@Query("userId") userId: Int = 0): Call<UserInfoByPart>

    //获取聊天记录
    @GET("getRecordByUserId")
    fun getMessageLog(@Query("sendUserId") sendUserId: Int = 0): Call<List<JsonObject>>

    //获取私聊记录
    @GET("getRecordByPoint")
    fun getMessageSingle(@Query("sendUserId") sendUserId: Int = 0, @Query("receiveUserId") receiveUserId: Int = 0):Call<List<JsonObject>>

    //获取群聊记录
    @GET("getRecordByAll")
    fun getMessageGroup(): Call<List<JsonObject>>

    //获取热门项目和用户
    @GET("getPopularAndNear")
    fun getPopularAndNear(@Query("region") region: String): Call<JsonObject>

    //获取省内项目
    @GET("getNearByProject")
    fun getNearByProject(@Query("area") area: String,@Query("pageNum") pageNum: Int,@Query("limit") limit: Int): Call<JsonObject>

    //匹配聊天
    @GET("getMatchUserId")
    fun getMatchUserId(@Query("userId") userId: Int = Attributes.userId): Call<Int>

    @GET("getProvinces")
    fun getOrgProvince():Call<JsonObject>

    @GET("getCities")
    fun getOrgCity(@Query("province") province: String):Call<JsonObject>

    @GET("getSchools")
    fun getOrgs(@Query("city") city: String):Call<JsonObject>

    companion object {
        private const val SERVER_URL = "" //server url
        val instance: RemoteService by lazy(LazyThreadSafetyMode.NONE) {
            val client = OkHttpClient.Builder()
                    .addInterceptor {
                        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger {
                            override fun log(message: String) {
                                Log.e("Retrofit",message)
                            }
                        })
                        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        loggingInterceptor.intercept(it)
                    }
            Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build().create(RemoteService::class.java)
        }

        fun loginByPass(loginRequest: LoginRequest) = instance.loginByPass(loginRequest)
        fun register(registerRequest: RegisterRequest) = instance.register(registerRequest)
        fun getPhoneCode(phoneNumber: String) = instance.getPhoneCode(phoneNumber)
        fun searchProjectByBlur(request: SearchRequest) = instance.searchProjectByBlur(request)
        fun initPersonInfo(initPersonInfoRequest: InitPersonInfoRequest) = instance.initPersonInfo(initPersonInfoRequest)
        fun updatePersonInfo(updateInfoRequest: UpdateInfoRequest) = instance.updatePersonInfo(updateInfoRequest)
        fun setupUserAvatar(file: MultipartBody.Part, token: RequestBody, username: RequestBody) = instance.setupUserAvatar(token, username,file)
        fun createProject(createProjectRequest: CreateProjectRequest) = instance.createProject(createProjectRequest)
        fun getProjectInfo(getProjectRequest: GetProjectRequest) = instance.getProjectInfo(getProjectRequest)
        fun updateProject(updateProjectRequest: UpdateProjectRequest) = instance.updateProject(updateProjectRequest)
        fun deleteProject(getProjectRequest: GetProjectRequest) = instance.deleteProject(getProjectRequest)
        fun getProjectComments(commentListRequest: CommentListRequest) = instance.getProjectComment(commentListRequest)
        fun createProjectComment(createCommentRequest: CreateCommentRequest) = instance.createProjectComment(createCommentRequest)
        fun getRecommendData(simpleRequest: SimpleRequest) = instance.getRecommendData(simpleRequest)
        fun addProjectPicture(part: ArrayList<MultipartBody.Part>, token: RequestBody, username: RequestBody, projectId: RequestBody) = instance.addProjectPicture(part, token, username, projectId)
        fun addProjectVideo(part: ArrayList<MultipartBody.Part>, token: RequestBody, username: RequestBody, projectId: RequestBody) = instance.addProjectVideo(part, token, username, projectId)
        fun resetPassword(registerRequest: RegisterRequest) = instance.resetPassword(registerRequest)
        fun joinProject(getProjectRequest: GetProjectRequest) = instance.joinProject(getProjectRequest)
        fun getCountAndPicture() = instance.getCountAndPicture()
        fun explorePublish(part: ArrayList<MultipartBody.Part>, token: RequestBody, dyContent: RequestBody) = instance.explorePublish(part, token, dyContent)
        fun getExploreList(pageRequest: PageRequest) = instance.getExploreList(pageRequest)
        fun getUserFavoriteList(pageRequest: PageRequest) = instance.getUserFavoriteList(pageRequest)
        fun getUserJoinedList(pageRequest: PageRequest) = instance.getUserJoinedList(pageRequest)
        fun getUserReleasedList(pageRequest: PageRequest) = instance.getUserReleasedList(pageRequest)
        fun getExploreComment(exploreCommentInfoRequest: ExploreCommentInfoRequest) = instance.getExploreComment(exploreCommentInfoRequest)
        fun exploreItemLike(exploreItemLikeRequest: ExploreItemLikeRequest) = instance.exploreItemLike(exploreItemLikeRequest)
        fun exploreItemFavorite(exploreItemFavoriteRequest: ExploreItemFavoriteRequest) = instance.exploreItemFavorite(exploreItemFavoriteRequest)
        fun exploreItemUnlike(exploreItemLikeRequest: ExploreItemLikeRequest) = instance.exploreItemUnlike(exploreItemLikeRequest)
        fun exploreItemUnfavorite(exploreItemFavoriteRequest: ExploreItemFavoriteRequest) = instance.exploreItemUnfavorite(exploreItemFavoriteRequest)
        fun commentExploreItem(exploreCommentRequest: ExploreCommentRequest) = instance.commentExploreItem(exploreCommentRequest)
        fun getHeadLinkAndNick(userId: Int) = instance.getHeadLinkAndNick(userId)
        fun getMessageLog(userId: Int) = instance.getMessageLog(userId)
        fun getMessageSingle(sendUserId: Int,receiveUserId: Int) = instance.getMessageSingle(sendUserId, receiveUserId)
        fun getMessageGroup() = instance.getMessageGroup()
        fun getPopularAndNear(region: String) = instance.getPopularAndNear(region)
        fun getNearByProject(area: String,pageNum: Int,limit: Int = 20) = instance.getNearByProject(area, pageNum, limit)
        fun getMatchUserId() = instance.getMatchUserId()
        fun getExploreDetails(exploreRequest: ExploreRequest) = instance.getExploreDetails(exploreRequest)
        fun delExploreItem(deleteRequest: ExploreDeleteRequest) = instance.delExploreItem(deleteRequest)
        fun getProjectJoinedMembers(projectRequest: GetProjectRequest) = instance.getProjectJoinedMember(projectRequest)
        fun getOtherUserReleasedList(simplePageRequest: SimplePageRequest) = instance.getOtherUserReleasedList(simplePageRequest)
        fun getOtherUserJoinedList(simplePageRequest: SimplePageRequest) = instance.getOtherUserJoinedList(simplePageRequest)
        fun updateExploreItem(exploreEditRequest: ExploreEditRequest) = instance.updateExploreItem(exploreEditRequest)
        fun deleteExploreComment(exploreCommentDeleteRequest: ExploreCommentDeleteRequest) = instance.delExploreComment(exploreCommentDeleteRequest)
        fun getOrgProvince() = instance.getOrgProvince()
        fun getOrgCity(province: String) = instance.getOrgCity(province)
        fun getOrg(city: String) = instance.getOrgs(city)
    }
}