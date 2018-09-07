package com.bigcreate.zyfw.models

data class Project(var project_id:String, var project_topic: String, var project_content: String, var project_picture_link: String,
                   var project_region: String, var project_address: String, var project_principal_name: String, var project_principal_phone:String,
                   var project_issue_time:String, var project_people_numbers : String, var prpject_user_id: Int,var username: String,
                   var videoBase64: String, var pictureBase64: String)