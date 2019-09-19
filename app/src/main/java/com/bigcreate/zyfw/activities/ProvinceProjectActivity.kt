package com.bigcreate.zyfw.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import com.amap.api.col.n3.it
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProvinceProjectAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.datasource.ProjectListDataSource
import com.bigcreate.zyfw.datasource.ProvinceProjectDataSource
import com.bigcreate.zyfw.fragments.ProvinceProjectDialogFragment
import com.bigcreate.zyfw.models.ProvinceProject
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.SearchRequest
import kotlinx.android.synthetic.main.activity_province_project.*

class ProvinceProjectActivity : AppCompatActivity() {
    private var province = Attributes.AppProvince
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_province_project)
        provinceProjectList.itemAnimator = DefaultItemAnimator()
        Attributes.addProvinceListener(javaClass.name) {
            province = it
            refreshList()
        }
        refreshProvince.setOnRefreshListener {
            refreshList()
            refreshProvince.isRefreshing = false
        }
    }

    fun refreshList() {
        provinceProjectList.adapter = ProvinceProjectAdapter() {
            ProvinceProjectDialogFragment().apply {
                this.pronviceProject = this@ProvinceProjectAdapter
                show(supportFragmentManager,"provinceProject")
            }
        }
                .apply {
                    submitList(PagedList.Builder<Int, ProvinceProject>(
                            ProvinceProjectDataSource(province),
                            PagedList.Config.Builder()
                                    .setPageSize(20)
                                    .setPrefetchDistance(40)
                                    .build()
                    ).setNotifyExecutor {
                        Handler(Looper.getMainLooper()).post(it)
                    }.setFetchExecutor {
                        Attributes.backgroundExecutors.execute(it)
                    }
                            .build())
                }
    }
    override fun onDestroy() {
        super.onDestroy()
        Attributes.removeProvinceListener(javaClass.name)
    }
}
