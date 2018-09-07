package com.bigcreate.zyfw.models

data class Comment(val project_id:String, val comment: String, val picture_link:String, val video_link:String,
                   val user_id:Int, val user_nick:String, val imgBase64: String, val videoBase64:String)