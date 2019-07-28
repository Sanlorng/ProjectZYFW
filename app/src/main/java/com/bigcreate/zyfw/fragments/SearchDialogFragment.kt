package com.bigcreate.zyfw.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.edit
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.library.toast
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.datasource.ProjectListDataSource
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.models.SearchResponse
import com.bigcreate.zyfw.mvp.app.AMapLocationImpl
import com.bigcreate.zyfw.mvp.project.SearchImpl
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_search_dialog.*
import kotlinx.android.synthetic.main.layout_search_bar.*

class SearchDialogFragment : DialogFragment() {
    private var searchKey = ""
    var location: AMapLocation? = null
    var isClick = false
    private var searchHistory = ArrayList<Pair<String, Long>>()
    private lateinit var networkStateViewModel: NetworkStateViewModel
    private val aMapLocationImpl = AMapLocationImpl(object : AMapLocationImpl.View {
        override val onceLocation: Boolean
            get() = false

        override fun onRequestFailed(location: AMapLocation?) {

        }

        override fun onRequestSuccess(location: AMapLocation) {
            this@SearchDialogFragment.location = location
            dialog?.apply {
                inputSearchBar.hint = "搜索 ${location.city}"
            }
        }

        override fun getViewContext(): Context {
            return context!!
        }
    })
    private var projectId = -1
    private val searchRequest = SearchRequest("", null, null, null, 1)
    private val searchView = object : SearchImpl.View {
        override fun onSearchFailed(response: JsonObject) {
            context?.toast(response.toJson())
            progressSearchDialog?.isVisible = false
        }

        override fun onSearchFinished(searchResponse: SearchResponse) {
            dialog?.apply {
                //                GlobalScope.launch {
//                    searchHistory = ArrayList(searchHistory.filter {
//                        it.first != searchKey
//                    }).apply {
//                        add(0, Pair(searchKey, System.currentTimeMillis()))
//                        if (searchHistory.size > 10)
//                            removeAt(10)
//                    }
//                    showSearchHistory(ad)
                addSearchHistory()
//                }
//                progressSearchDialog.isVisible = false
//                searchResponse.list.isEmpty().apply {
//                    textSearchEmpty.isVisible = this
//                }
//                inputSearchBar.clearFocus()
//                listSearchResult.apply {
//                    layoutManager = LinearLayoutManager(context)
//                    searchResponse.list.apply {
//                        adapter = ProjectListAdapter(this).apply {
//                            mListener = object : ProjectListAdapter.ProjectItemClickListener {
//                                override fun onItemClick(position: Int) {
//                                    isClick = true
//                                    startActivityForResult(Intent(context, ProjectDetailsActivity::class.java).apply {
//                                        get(position).run {
//                                            this@SearchDialogFragment.projectId = projectId
//                                            putExtra("position", position)
//                                            putExtra("projectId", projectId)
//                                            putExtra("projectTopic", projectTopic)
//                                        }
//                                    }, RequestCode.OPEN_PROJECT)
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }

        override fun getViewContext(): Context {
            return context!!
        }

        override fun onNetworkFailed() {

        }

        override fun onRequesting() {
            dialog.apply {
                progressSearchDialog?.isVisible = true
            }

        }

        override fun onRequestFinished() {
            swipeLayoutSearch?.isRefreshing = false
        }
    }
    private val searchImpl = SearchImpl(searchView)
    //    private val locationView = object : LocationContract.View {
//        override fun onLocationPermissionDenied() {
//            context?.toast("位置权限被禁止")
//        }
//
//        override fun onLocationRequestFailed() {
////            context?.toast("位置信息请求失败")
//        }
//
//        override fun onLocationRequestSuccess(location: TencentLocation) {
//            dialog?.apply {
//                inputSearchBar.hint = getString(R.string.search) + " " + location.city
//            }
//            this@SearchDialogFragment.location = location
//        }
//
//        override fun getViewContext(): Context {
//            return context!!
//        }
//
//        override fun onRequesting() {
//
//        }
//
//        override fun onRequestFinished() {
//        }
//
//    }
//    private val locationImpl = LocationImpl(locationView)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!, R.style.bottomDialog).apply {
            networkStateViewModel = ViewModelProviders.of(this@SearchDialogFragment).get(NetworkStateViewModel::class.java)
            networkStateViewModel.state.observe(this@SearchDialogFragment, Observer {
                when (it.status) {
                    Status.SUCCESS -> showProgress(false)
                    Status.FAILED -> {
                        showProgress(false)
                        toast(it.msg)
                    }
                    Status.RUNNING -> showProgress(true)
                }
            })
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.fragment_search_dialog)
            setCanceledOnTouchOutside(true)
//            locationImpl.mView = locationView
            searchImpl.mView = searchView
//            locationImpl.start()
//            locationImpl.alwaysCall = true
            window?.apply {
                attributes.gravity = Gravity.TOP
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
            toolbarSearchBar.navigationIcon = context.getDrawable(R.drawable.ic_arrow_back_black_24dp)
            toolbarSearchBar.setNavigationOnClickListener {
                this@SearchDialogFragment.onBackPressed()
            }
            toolbarSearchBar.inflateMenu(R.menu.clean_menu)
            toolbarSearchBar.menu.findItem(R.id.searchClear).isVisible = false
            toolbarSearchBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.searchClear -> {
                        inputSearchBar.editableText.clear()
                    }
                }
                true
            }
            (cardViewAppBarMain.layoutParams as AppBarLayout.LayoutParams).apply {
                setMargins(context.resources.getDimensionPixelSize(R.dimen.appbar_margin))
//                Log.e("top", topMargin.toString())
//                Log.e("bottom", bottomMargin.toString())
//                Log.e("start", marginStart.toString())
//                Log.e("end", marginEnd.toString())
                cardViewAppBarMain.layoutParams = this
            }
            inputSearchBar.isEnabled = true
            inputSearchBar.isClickable = true
            inputSearchBar.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    val city = when {
                        location == null -> null
                        location!!.city == "Unknown" && BuildConfig.DEBUG -> "贺州市"
                        else -> location!!.city
                    }
                    if (!inputSearchBar.editableText.trimEnd().isEmpty())
                        searchRequest.apply {
                            searchKey = inputSearchBar.editableText.trimEnd().toString()
                            token = Attributes.token
                            projectRegion = city
                            projectTopic = searchKey
                            projectContent = null
                            reSearch()
                        }
                    true
                } else
                    false
            }
            inputSearchBar.addTextChangedListener {
                toolbarSearchBar.menu.findItem(R.id.searchClear).isVisible = !it.isNullOrEmpty()
            }
            inputSearchBar.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    showSearchHistory(true)
                    swipeLayoutSearch.isVisible = false
                } else {
                    showSearchHistory(false)
                    swipeLayoutSearch.isVisible = true
                    context.getSystemService(InputMethodManager::class.java).hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
            swipeLayoutSearch.setOnRefreshListener {
                if (!inputSearchBar.editableText.trimEnd().isEmpty())
                    reSearch()
                else
                    swipeLayoutSearch.isRefreshing = false
            }
            actionClearSearchHistory.setOnClickListener {
                searchHistory.clear()
                chipGroupSearchHistory.removeAllViews()
                showSearchHistory(false)
            }
            val searchMapString = context.defaultSharedPreferences.getString("searchHistory", null)
            if (searchMapString != null) {
                Log.e("searchHistory", searchMapString)
                searchHistory = searchMapString.fromJson<ArrayList<Pair<String, Long>>>()
            }
//            val layoutParam = cardViewAppBarMain.layoutParams as AppBarLayout.LayoutParams
//            layoutParam.topMargin += context.let {
//                it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height", "dimen", "android"))
//            }
//            cardViewAppBarMain.layoutParams = layoutParam
//            aMapLocationImpl = AMapLocationImpl(object :AMapLocationImpl.View {
//                override val onceLocation: Boolean
//                    get() = false
//
//                override fun getViewContext(): Context {
//                    return context
//                }
//
//                override fun onRequestSuccess(location: AMapLocation) {
//                    inputSearchBar.hint = "搜索 ${location.city}"
//                }
//
//                override fun onRequestFailed(location: AMapLocation?) {
//
//                }
//            })
            if (BuildConfig.DEBUG)
                Log.e("onCreateDialog", "true")
        }

    }

    private fun showProgress(boolean: Boolean) {

    }

    private fun reSearch() {
        searchRequest.projectRegion = Attributes.AppCity
        dialog?.apply {

            searchRequest.projectTopic = inputSearchBar?.editableText?.trim().toString()
            listSearchResult.layoutManager = LinearLayoutManager(context)
            listSearchResult.adapter = ProjectListAdapter().apply {
                submitList(PagedList.Builder<Int, SearchModel>(ProjectListDataSource(searchRequest,
                        networkStateViewModel.state),
                        PagedList.Config.Builder()
                                .setPageSize(10)
                                .setPrefetchDistance(20)
                                .build())
                        .setNotifyExecutor {
                            Handler(Looper.getMainLooper()).post(it)
                        }
                        .setFetchExecutor {
                            Attributes.backgroundExecutors.execute(it)
                        }.build())
            }
            listSearchResult.isVisible = true
        }
    }

    override fun onResume() {
        if (BuildConfig.DEBUG)
            Log.e("onResume", "true")
        dialog?.apply {
            //            locationImpl.doLocationRequest()
            aMapLocationImpl.startLocation()
            if (isClick.not())
                inputSearchBar.requestFocus()
            window?.translucentSystemUI(true)
            if (inputSearchBar.hasFocus())
                showSearchHistory(true)
        }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSearchHistory()
        showSearchHistory(true)
    }

    fun onBackPressed(): Boolean {
        Log.e("onBackPressed", (dialog == null).not().toString())
        dialog?.apply {
            Log.e("swipe", swipeLayoutSearch.isVisible.not().toString())
            return if (inputSearchBar.hasFocus() && listSearchResult.childCount != 0) {
                //                showSearchHistory(false)
                //                swipeLayoutSearch?.isVisible = true
                if (BuildConfig.DEBUG)
                    Log.e("focus", "true")
                inputSearchBar.clearFocus()
                true
            } else {
                this@SearchDialogFragment.dismiss()
                if (BuildConfig.DEBUG)
                    Log.e("unFocus", "true")
                false
            }
        }
        return false
    }

    override fun onDismiss(dialog: DialogInterface) {
        searchImpl.detachView()
//        locationImpl.detachView()
        aMapLocationImpl.stopLocation()
        context?.defaultSharedPreferences?.edit {
            putString("searchHistory", searchHistory.toJson())
        }
        isClick = false
        super.onDismiss(dialog)
    }

    private fun addSearchHistory() {
        if (BuildConfig.DEBUG)
            Log.e("onSearchKey", searchHistory.size.toString())
        searchHistory = ArrayList(searchHistory.filter {
            it.first != searchKey
        }).apply {
            if (searchKey.isEmpty().not())
                add(0, Pair(searchKey, System.currentTimeMillis()))
            if (size > 10)
                removeAt(10)
            dialog?.apply {
                chipGroupSearchHistory.removeAllViews()
                if (size > 0)
                    for (i in 0 until size)
                        chipGroupSearchHistory.addView(
                                Chip(context).apply {
                                    id = i
                                    var tempText = get(i).first
                                    if (tempText.length > 8)
                                        tempText = tempText.substring(0, 7) + "..."
                                    text = tempText
                                    setOnClickListener {
                                        inputSearchBar.editableText.clear()
                                        inputSearchBar.editableText.append(get(it.id).first)
                                    }
                                    isCloseIconVisible = true
                                    setOnCloseIconClickListener {
                                        searchHistory.removeAt(it.id)
                                        chipGroupSearchHistory.removeViewAt(it.id)
                                        for (index in 0 until searchHistory.size)
                                            chipGroupSearchHistory[index].id = index
                                    }
                                }
                        )
            }
        }
    }

    //    private fun showSearchHistory(dialog: Dialog) {
//        dialog.apply {
//            searchHistory.apply {
//                val last = when {
//                    size == 0 -> 0
//                    size > 10 -> 9
//                    else -> size - 1
//                }
//                GlobalScope.launch(Dispatchers.Main) {
//                    chipGroupSearchHistory.isVisible = true
//                    chipGroupSearchHistory.removeAllViews()
//                    if (size > 0) {
//                        for (i in 0..last)
//                            chipGroupSearchHistory.addView(
//                                    Chip(context).apply {
//                                        id = i
//                                        var tempText = get(i).first
//                                        if (tempText.length > 8)
//                                            tempText = tempText.substring(0, 7) + "..."
//                                        text = tempText
//                                        setOnClickListener {
//                                            inputSearchBar.editableText.clear()
//                                            inputSearchBar.editableText.append(get(i).first)
//                                        }
//                                        isCloseIconVisible = true
//                                        setOnCloseIconClickListener {
//                                            searchHistory.removeAt(it.id)
//                                            chipGroupSearchHistory.removeViewAt(it.id)
//                                        }
//                                    }
//                            )
//                        actionClearSearchHistory.setOnClickListener {
//                            searchHistory.clear()
//                            chipGroupSearchHistory.removeAllViews()
//                            layoutSearchHistory.isVisible = false
//                            chipGroupSearchHistory.isVisible = false
//                        }
//                        layoutSearchHistory.isVisible = true
//                    }
//                }
//            }
//        }
//    }
    private fun showSearchHistory(value: Boolean) {
        dialog?.apply {
            if (BuildConfig.DEBUG) {
                Log.e("isShow", value.toString())
                Log.e("historyNum", searchHistory.size.toString())
                Log.e("num", chipGroupSearchHistory.childCount.toString())
            }
            if (searchHistory.size != chipGroupSearchHistory.size && value)
                addSearchHistory()
            chipGroupSearchHistory?.isVisible = value
            layoutSearchHistory?.isVisible = value
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        dialog?.apply {
            inputSearchBar?.clearFocus()
            if (requestCode == RequestCode.OPEN_PROJECT && resultCode == ResultCode.OK) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}