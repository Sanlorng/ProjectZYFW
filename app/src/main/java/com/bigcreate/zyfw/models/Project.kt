package com.bigcreate.zyfw.models

data class Project(var projectId:String, var projectTopic: String, var projectContent: String,
                   var projectRegion: String, var projectAddress: String, var latitude: Double, var longitude: Double, var projectPrincipalName: String, var projectPrincipalPhone:String,
                   var projectIssueTime:String, var projectPeopleNumbers : String, var projectUserId: Int, var projectTypeId: Int, var username: String,
                   var videoBase64: String, var pictureBase64: String)
