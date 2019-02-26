package com.bigcreate.zyfw.models

data class Project(
        val latitude: Double,
        val longitude: Double,
        val pictureBase64: String,
        val projectAddress: String,
        val projectContent: String,
        val projectId: Int,
        val projectIssueTime: String,
        val projectPeopleNumbers: String,
        val projectPictureLink: String,
        val projectPrincipalName: String,
        val projectPrincipalPhone: String,
        val projectRegion: String,
        val projectTopic: String,
        val projectTypeId: Int,
        val projectVideoLink: String,
        val token: String,
        val username: String,
        val videoBase64: String
)
