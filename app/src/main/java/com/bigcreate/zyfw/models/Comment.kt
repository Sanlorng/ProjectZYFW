package com.bigcreate.zyfw.models

data class Comment(val projectId: String, val comment: String, val commentTime: String?, val picture_link: String?, val video_link: String?,
                   val userId: Int, val userNick: String, val imgBase64: String?, val videoBase64: String?)