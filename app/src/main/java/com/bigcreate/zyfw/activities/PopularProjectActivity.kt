package com.bigcreate.zyfw.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bigcreate.library.fromJson
import com.bigcreate.library.startActivity
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.AvatarListAdapter
import com.bigcreate.zyfw.adapter.ProjectAdapter
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.jsonData
import com.bigcreate.zyfw.callback.enqueue
import com.bigcreate.zyfw.models.PolularProjectResponse
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.UserInfoByPart
import kotlinx.android.synthetic.main.activity_popular_project.*
import kotlinx.android.synthetic.main.fragment_message.*

class PopularProjectActivity : AppCompatActivity() {
    private val userList = ArrayList<UserInfoByPart>()
    private val projectList = ArrayList<SearchModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_project)
        window.translucentSystemUI(true)
        setSupportActionBar(popularProjectToolbar)
        supportActionBar?.title = "热门项目"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        popularProjectToolbar.setNavigationOnClickListener {
            finish()
        }
        popularProjectList.itemAnimator = DefaultItemAnimator()
        popularProjectList.adapter = ProjectAdapter(projectList) {
            startActivity<ProjectDetailsActivity> {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(Uri.parse(String.format(Attributes.authorityProject, projectId)), "project/${projectTopic}")
                putExtra("projectId", projectId)
            }
        }
        nearUserList.itemAnimator = DefaultItemAnimator()
        nearUserList.adapter = AvatarListAdapter(userList) {
            nearUserList.context.startActivity<MyDetailsActivity> {
                putExtra("userId", userId)
            }
        }
        Attributes.addCityListener(javaClass.name) {
            RemoteService.getPopularAndNear(it).enqueue {
                error {
                    refreshPopular.isRefreshing = false
                }
                response {
                    refreshPopular.isRefreshing = false
                    body()?.apply {
                        if (code == 200) {
                            refreshList(jsonData.toString().fromJson())
                        }
                    }
                }
            }
        }
    }

    private fun refreshList(popularProjectResponse: PolularProjectResponse?) {
        popularProjectResponse?.apply {
            userList.clear()
            userList.addAll(content.people)
            nearUserList.adapter?.notifyDataSetChanged()
            projectList.clear()
            projectList.addAll(content.popula)
            popularProjectList.adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Attributes.removeCityListener(javaClass.name)
    }
}
