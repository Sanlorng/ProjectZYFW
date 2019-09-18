package com.bigcreate.zyfw.models

data class Project(
        val join: Boolean,
        val joinedNumber: Int,
        val favorite: Boolean,
        val latitude: Double,
        val longitude: Double,
        val pageNum: Int,
        val projectAddress: String,
        val projectContent: String,
        val projectId: Int,
        val projectIssueTime: String,
        val projectPeopleNumbers: String,
        val projectPictureLink: Any,
        val projectPictureLinkTwo: List<String>,
        val projectPrincipalName: String,
        val projectPrincipalPhone: String,
        val projectRegion: String,
        val projectTopic: String,
        val projectTypeId: Int,
        val projectVideoLink: Any,
        val projectVideoLinkTwo: List<String>,
        val token: String?,
        val username: String,
        val userInfoByPart: UserInfoByPart
)

data class SearchResponse(
        val content: SearchContent,
        val newToken: String
)

data class SearchContent(
        val endRow: Int,
        val firstPage: Int,
        val hasNextPage: Boolean,
        val hasPreviousPage: Boolean,
        val isFirstPage: Boolean,
        val isLastPage: Boolean,
        val lastPage: Int,
        val list: List<SearchModel>,
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

data class X(
        val favorite: Any,
        val join: Any,
        val joinedNumber: Int,
        val latitude: Double,
        val longitude: Double,
        val pageNum: Int,
        val projectAddress: String,
        val projectContent: String,
        val projectId: Int,
        val projectIssueTime: String,
        val projectPeopleNumbers: String,
        val projectPictureLink: Any,
        val projectPictureLinkTwo: List<Any>,
        val projectPrincipalName: String,
        val projectPrincipalPhone: String,
        val projectRegion: String,
        val projectTopic: String,
        val projectTypeId: Int,
        val projectVideoLink: Any,
        val projectVideoLinkTwo: List<Any>,
        val token: Any,
        val username: String
)
//data class Project(
//        val latitude: Double,
//        val longitude: Double,
//        val pictureBase64: String,
//        val projectAddress: String,
//        val projectContent: String,
//        val projectId: Int,
//        val projectIssueTime: String,
//        val projectPeopleNumbers: String,
//        val projectPictureLink: String,
//        val projectPrincipalName: String,
//        val projectPrincipalPhone: String,
//        val projectRegion: String,
//        val projectTopic: String,
//        val projectTypeId: Int,
//        val projectVideoLink: String,
//        val token: String,
//        val username: String,
//        val videoBase64: String,
//        var favorite: Boolean
//)
