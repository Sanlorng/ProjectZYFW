package com.bigcreate.zyfw.models

import com.bigcreate.library.transDate

data class SearchModel(
        val projectId: Int,
        val projectTopic: String,
        val projectPeopleNumbers: String,
        val projectAddress: String,
        val projectPictureLinkTwo: List<String>,
        val projectContent: String,
        val projectPrincipalName: String,
        var projectIssueTime: String) {
    init {
        projectIssueTime = projectIssueTime.transDate("yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd")
    }
}

data class CountAndPictureModel(val volunteerCount: Int, val WheelPicture: List<String>)
data class ProjectListCommentResponse(
        val content: ProjectListCommentContent,
        val newToken: String
)
data class ListDataContent<T> (
        val endRow: Int,
        val firstPage: Int,
        val hasNextPage: Boolean,
        val hasPreviousPage: Boolean,
        val isFirstPage: Boolean,
        val isLastPage: Boolean,
        val lastPage: Int,
        val list: List<T>,
        val navigateFirstPage: Int,
        val navigateLastPage: Int,
        val navigatePages: Int,
        val navigatepageNums: List<Int>,
        val nextPage: Int,
        val pageNum: Int,
        val pageSize: Int,
        val pages: Int,
        val prePage: Int,
        val size: Int,
        val startRow: Int,
        val total: Int
)
data class ProjectListCommentContent(
        val endRow: Int,
        val firstPage: Int,
        val hasNextPage: Boolean,
        val hasPreviousPage: Boolean,
        val isFirstPage: Boolean,
        val isLastPage: Boolean,
        val lastPage: Int,
        val list: List<Comment>,
        val navigateFirstPage: Int,
        val navigateLastPage: Int,
        val navigatePages: Int,
        val navigatepageNums: List<Int>,
        val nextPage: Int,
        val pageNum: Int,
        val pageSize: Int,
        val pages: Int,
        val prePage: Int,
        val size: Int,
        val startRow: Int,
        val total: Int
)

data class LoginModel(var username: String, var password: String, var token: String, var userId: Int)
data class test(
        val name: String,
        val sub: List<Sub>,
        val type: Int
)

data class Sub(
        val name: String
)

data class Province(
        val city: List<City>,
        val name: String
)

data class City(
        val area: List<String>,
        val name: String
)

data class ExploreListResponse(
        val content: ExploreContent,
        val newToken: String
)

data class ExploreContent(
        val endRow: Int,
        val firstPage: Int,
        val hasNextPage: Boolean,
        val hasPreviousPage: Boolean,
        val isFirstPage: Boolean,
        val isLastPage: Boolean,
        val lastPage: Int,
        val list: List<ExploreItem>,
        val navigateFirstPage: Int,
        val navigateLastPage: Int,
        val navigatePages: Int,
        val navigatepageNums: List<Int>,
        val nextPage: Int,
        val pageNum: Int,
        val pageSize: Int,
        val pages: Int,
        val prePage: Int,
        val size: Int,
        val startRow: Int,
        val total: Int
)

data class PageListResponse<T>(
        val content: PageContent<T>,
        val newToken: String
)

data class PageContent<T>(
        val endRow: Int,
        val firstPage: Int,
        val hasNextPage: Boolean,
        val hasPreviousPage: Boolean,
        val isFirstPage: Boolean,
        val isLastPage: Boolean,
        val lastPage: Int,
        val list: List<T>,
        val navigateFirstPage: Int,
        val navigateLastPage: Int,
        val navigatePages: Int,
        val navigatepageNums: List<Int>,
        val nextPage: Int,
        val pageNum: Int,
        val pageSize: Int,
        val pages: Int,
        val prePage: Int,
        val size: Int,
        val startRow: Int,
        val total: Int
)

data class ExploreItem(
        val dyContent: String,
        val dyId: Int,
        val dyOneLink: Any,
        val dyReleaseTime: String,
        val dyReleaseUserId: Int,
        val dynamicPicture: List<DynamicPicture>,
        var favorite: Boolean,
        var praise: Boolean,
        val userInfoByPart: UserInfoByPart
)

data class UserInfoByPart(
        val userHeadPictureLink: String,
        val userId: Int,
        val userNick: String
)

data class DynamicPicture(
        val dyPictureId: Int,
        val dyPictureOneLink: Any,
        val dyPictureTwoLink: String
)


