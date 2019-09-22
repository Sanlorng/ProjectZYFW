package com.bigcreate.zyfw.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bigcreate.library.toast
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProjectJoinedMemberAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.JoinedMember
import com.bigcreate.zyfw.models.UserInfoByPart
import com.bigcreate.zyfw.mvp.project.GetJoinedMembersImpl
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_project_joined_member.*

class ProjectJoinedMemberActivity : AppCompatActivity(),GetJoinedMembersImpl.View {
    private val getJoinedMembersImpl = GetJoinedMembersImpl(this)
    private var projectId = -1
    override fun onGetJoinedMemberFailed(jsonObject: JsonObject) {
        toast("获取失败")
    }

    override fun onGetJoinedMemberSuccess(userList: List<JoinedMember>) {
        supportActionBar?.subtitle = "${userList.size} 人已加入"
        listJoinedMember.adapter = ProjectJoinedMemberAdapter(userList)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_joined_member)
        window.translucentSystemUI(true)
        setSupportActionBar(toolbarProjectJoinedMember)
        supportActionBar?.title = "招募 ${intent.getStringExtra("number")?:"0"} 人"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarProjectJoinedMember.setNavigationOnClickListener {
            finish()
        }
        projectId = intent.getIntExtra("projectId",projectId)
        getJoinedMembersImpl.doRequest(GetProjectRequest(Attributes.token,projectId))
        refreshJoinedMember.setOnRefreshListener {
            getJoinedMembersImpl.doRequest(GetProjectRequest(Attributes.token,projectId))
        }
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        getJoinedMembersImpl.detachView()
    }
}
