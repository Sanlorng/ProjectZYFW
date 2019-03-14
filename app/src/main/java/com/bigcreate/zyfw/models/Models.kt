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
        var projectIssueTime: String){
    init {
        projectIssueTime = projectIssueTime.transDate("yyyy-MM-dd HH:mm:ss","yyyy.MM.dd")
    }
}
data class LoginModel(var username: String, var password: String, var token: String)