package com.bigcreate.zyfw.models

data class ExploreCommentItem(
        val dyCommentId:Int,
        val dyCommentContent:String,
        val dyCommentTime:String,
        val dyCommentUserId:Int,
        val dyCommentUserNick:String,
        val token:String ?= null
)