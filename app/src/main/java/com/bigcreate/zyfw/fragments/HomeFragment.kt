package com.bigcreate.zyfw.fragments


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.bigcreate.library.fromJson
import com.bigcreate.library.startActivity
import com.bigcreate.library.statusBarHeight
import com.bigcreate.library.toast
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.*
import com.bigcreate.zyfw.adapter.MenuListAdapter
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.Status
import com.bigcreate.zyfw.base.paddingStatusBar
import com.bigcreate.zyfw.datasource.ProjectListDataSource
import com.bigcreate.zyfw.models.*
import com.bigcreate.zyfw.mvp.app.AMapLocationImpl
import com.bigcreate.zyfw.mvp.app.GetCountAndPictureImpl
import com.bigcreate.zyfw.mvp.project.SearchImpl
import com.bigcreate.zyfw.view.LoopViewPagerImageAdapter
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_org_select.*
import kotlinx.android.synthetic.main.layout_org_select.view.*
import kotlinx.android.synthetic.main.layout_search_bar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), SearchImpl.View, MainActivity.ChildFragment {

    private val mTag = "homeFragment"
    var projectId = -1
    private var imageList = List(0) {
        ""
    }
    private lateinit var networkStateViewModel: NetworkStateViewModel
    private var location: AMapLocation? = null
//    private lateinit var searchDialogFragment: SearchDialogFragment
    private lateinit var citySelectFragment: CitySelectFragment
    private lateinit var cityDialog:Dialog
    private val searchImpl = SearchImpl(this)
    private val getCountAndPictureImpl = GetCountAndPictureImpl(object : GetCountAndPictureImpl.View {
        override fun getViewContext(): Context {
            return context!!
        }

        override fun onLoadFailed() {

        }

        override fun onLoadSuccess(result: RestResult<CountAndPictureModel>) {
            result.data.apply {
                textVolunteerCount.text = String.format("共有 %s 名志愿者", volunteerCount.toString())
                imageList = WheelPicture
                viewPagePictureHome.adapter = LoopViewPagerImageAdapter(List(imageList.size) {
                    LoopViewPagerImageAdapter.ImageWrapper(imageList[it], "")
                })
            }
        }
    })
    private val aMapLocationImpl = AMapLocationImpl(object : AMapLocationImpl.View {
        override val onceLocation: Boolean
            get() = true

        override fun onRequestFailed(location: AMapLocation?) {

        }

        override fun onRequestSuccess(location: AMapLocation) {
            this@HomeFragment.location = location

//            reSearch()
        }

        override fun getViewContext(): Context {
            return context!!
        }
    })
//    private val menuList: ArrayList<MenuListAdapter.MenuItem> = arrayListOf(
//            MenuListAdapter.MenuItem(R.,
//                    R.drawable.ic_star_border_black_24dp,
//                    R.string.favoriteProject),
//            MenuListAdapter.MenuItem(R.id.userJoinedNavigation,
//                    R.drawable.ic_favorite_border_black_24dp,
//                    R.string.joinedProject))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        networkStateViewModel = ViewModelProvider(this).get(NetworkStateViewModel::class.java)
//        searchDialogFragment = SearchDialogFragment()
        citySelectFragment = CitySelectFragment()
        listProjectMain.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
//        fabStartReleaseMain.setOnClickListener {
//            startActivity(ReleaseProjectActivity::class.java)
//
        hintSearchBar.isVisible = true
        inputSearchBar.isVisible = false
        hintSearchBar.setOnClickListener {
            //searchDialogFragment.show(childFragmentManager, "searchDialogFragment")
            startActivity<SearchActivity>()
        }
//        listActionsHome.layoutManager = GridLayoutManager(context!!, 4)
//        listActionsHome.adapter = MenuListAdapter(menuList) {
//
//        }
//        val layoutParamA = appBarHome.layoutParams as CoordinatorLayout.LayoutParams
//        layoutParamA.topMargin += context?.statusBarHeight?:0
//        appBarHome.layoutParams = layoutParamA
//        val layoutParam = collapsingHome.layoutParams as AppBarLayout.LayoutParams
//        layoutParam.topMargin += context?.statusBarHeight?:0
//        collapsingHome.layoutParams = layoutParam
//        collapsingHome.paddingStatusBar()
        appBarHome.paddingStatusBar()
        viewPagePictureHome.showTitle = false
//        swipeLayoutMain.apply {
//            appBarHome.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
//            setProgressViewEndTarget(true,appBarHome.measuredHeight + progressViewEndOffset)
//            Log.e("paddingHeight",paddingTop.toString())
//        }
//        listProjectMain.apply {
//            setPadding(paddingLeft,paddingTop + appBarHome.measuredHeight +
//                    resources.getDimensionPixelOffset(resources.
//                            getIdentifier("status_bar_height", "dimen", "android")),
//                    paddingRight,paddingBottom)
//        }
        networkStateViewModel.state.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> showProgress(false)
                Status.FAILED -> {
                    showProgress(false)
                    toast(it.msg)
                }
                Status.RUNNING -> showProgress(true)
            }
        })
        Attributes.loginUserInfo?.apply {
            onLoginSuccess()
        }

        val provinceView = layoutInflater.inflate(R.layout.layout_org_select,null,false)
        provinceView.layoutDropdownOrg.isVisible = false
        provinceView.layoutDropdownOrgProvince.hint = "省份/直辖市/自治区"
        provinceView.layoutDropdownOrgCity.hint = "城市"
        cityDialog = MaterialAlertDialogBuilder(provinceView.context)
                .setView(provinceView)
                .setTitle("选择城市")
                .setPositiveButton("确定") { dialog, which ->
                    val string = provinceView.dropdownOrgCity.text.toString()
                    if (string.isNotBlank()) {
                        Attributes.AppCity = string
                    }
                }
                .setNegativeButton("取消") { dialog, which ->

                }
                .create()
        lifecycleScope.launch {
            try {
                var cityJson = ""
                val inputStream = provinceView.context.assets.open("cityList.json")
                val buffer = BufferedReader(InputStreamReader(inputStream))
                var temp = buffer.readLine()
                while (temp != null) {
                    cityJson += temp
                    temp = buffer.readLine()
                }
                inputStream.close()
                val cityList = cityJson.fromJson<List<Province>>()
                val provinceList = arrayListOf<String>()
                cityList.forEach {
                    provinceList.add(it.name)
                }
                withContext(Dispatchers.Main) {
                    provinceView.dropdownOrgProvince.setAdapter(ArrayAdapter(provinceView.context,R.layout.dropdown_menu_popup_item,provinceList))
                    provinceView.dropdownOrgProvince.setOnItemClickListener { parent, view, position, id ->
                        val cities = arrayListOf<String>()
                        cityList[position].city.forEach {
                            cities.add(it.name)
                        }
                        provinceView.dropdownOrgCity.text.clear()
                        provinceView.dropdownOrgCity.setAdapter(ArrayAdapter(provinceView.context,R.layout.dropdown_menu_popup_item,cities))
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showProgress(progressing: Boolean) {
        swipeLayoutMain.isRefreshing = progressing
        progressLoading.isVisible = progressing
        layoutLoading.isVisible = progressing
    }

    override fun getViewContext(): Context {
        return context!!
    }

    override fun onSearchFailed(response: JsonObject) {
        progressLoading.isVisible = false
        textLoading.text = "搜索失败，请点击重试"
    }

    private fun reSearch() {
//        searchImpl.doRequest(SearchRequest(Attributes.loginUserInfo!!.token, Attributes.AppCity, null, null))
        listProjectMain.layoutManager = LinearLayoutManager(context)
        listProjectMain.adapter = ProjectListAdapter { _, item ->
            startActivity<ProjectDetailsActivity> {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(Uri.parse(String.format(Attributes.authorityProject, item.projectId)), "project/${item.projectTopic}")
                putExtra("projectId", item.projectId)
            }
        }.apply {
            submitList(PagedList.Builder<Int, SearchModel>(
                    ProjectListDataSource(SearchRequest(Attributes.loginUserInfo!!.token, Attributes.AppCity, null, null, 1),
                            networkStateViewModel.state),
                    PagedList.Config.Builder()
                            .setPageSize(10)
                            .setPrefetchDistance(20)
                            .build()
            ).setNotifyExecutor {
                Handler(Looper.getMainLooper()).post(it)
            }.setFetchExecutor {
                Attributes.backgroundExecutors.execute(it)
            }
                    .build())
        }
    }

    override fun onRequesting() {
        super.onRequesting()
        if (swipeLayoutMain.isRefreshing.not())
            showProgress(true)
        textLoading.text = "正在搜索附近的项目"
        textLoading.setOnClickListener { }

    }

    override fun onRequestFinished() {
        super.onRequestFinished()
        showProgress(false)
    }

    override fun onSearchFinished(searchResponse: SearchResponse) {

        showProgress(false)
//        searchResponse.run {
//            listProjectMain.layoutManager = LinearLayoutManager(context)
//            listProjectMain.adapter = ProjectListAdapter(list).apply {
//                mListener = object : ProjectListAdapter.ProjectItemClickListener {
//                    override fun onItemClick(position: Int) {
//                        startActivityForResult(Intent(context, ProjectDetailsActivity::class.java).apply {
//                            list[position].run {
//                                putExtra("position", position)
//                                putExtra("projectId", projectId)
//                                putExtra("projectTopic", projectTopic)
//                                this@HomeFragment.projectId = projectId
//                            }
//                        }, RequestCode.OPEN_PROJECT)
//                    }
//                }
//            }
//        }
    }

    override fun onLoginSuccess() {
        getCountAndPictureImpl.doRequest("")
        aMapLocationImpl.startLocation()
        Attributes.loginUserInfo?.apply {
            swipeLayoutMain.setOnRefreshListener {
                reSearch()
            }
        }
        Attributes.addCityListener(mTag) {
            textCityHome.text = it
            reSearch()
        }
        textCityHome.setOnClickListener {
//            startActivity<CitySelectActivity>()
            cityDialog.show()
            cityDialog.dropdownOrgCity.dropDownHeight = resources.displayMetrics.heightPixels / 3
            cityDialog.dropdownOrgProvince.dropDownHeight = resources.displayMetrics.heightPixels / 3
//            citySelectFragment.show(childFragmentManager, "citySelectFragment")
        }

        provinceProject.setOnClickListener {
            it.context.startActivity<ProvinceProjectActivity>()
        }

        popularProject.setOnClickListener {
            it.context.startActivity<PopularProjectActivity>()
        }
    }


    override fun onDestroy() {
        aMapLocationImpl.detachView()
        searchImpl.detachView()
        Attributes.removeCityListener(mTag)
        super.onDestroy()
    }

}
