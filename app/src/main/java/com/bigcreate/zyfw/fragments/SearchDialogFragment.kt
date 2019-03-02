package com.bigcreate.zyfw.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.library.toast
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.MainActivity
import com.bigcreate.zyfw.activities.ProjectDetailsActivity
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.mvp.app.LocationContract
import com.bigcreate.zyfw.mvp.app.LocationImpl
import com.bigcreate.zyfw.mvp.project.SearchImpl
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import com.tencent.map.geolocation.TencentLocation
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.fragment_search_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchDialogFragment : DialogFragment() {
    var searchKey = ""
    var location: TencentLocation? = null
    var searchHistory = ArrayList<Pair<String, Long>>()
    private var projectId = -1
    private val searchRequest = SearchRequest(Attributes.loginUserInfo!!.token, null, null, null)
    private val searchView = object : SearchImpl.View {
        override fun onSearchFailed(response: JsonObject) {
            context?.toast(response.toJson())
            progressBarSearch?.isVisible = false
        }

        override fun onSearchFinished(searchResult: ArrayList<SearchModel>) {
            dialog.apply {
                GlobalScope.launch {
                    //                    if (searchMap.containsKey(searchKey))
//                        searchMap.remove(searchKey)
//                    searchMap.put(searchKey,System.currentTimeMillis())
//                    if (searchMap.size>10)
//                        searchMap.remove(searchMap.keys.first())
                    searchHistory = ArrayList(searchHistory.filter {
                        it.first != searchKey
                    }).apply {
                        add(0, Pair(searchKey, System.currentTimeMillis()))
                        if (searchHistory.size > 10)
                            removeAt(10)
                    }
                    showSearchHistory(this@apply)
                }
                progressBarSearch.isVisible = false
                searchResult.isEmpty().apply {
                    textSearchEmpty.isVisible = this
                }
                searchResultListView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = ProjectListAdapter(searchResult).apply {
                        mListener = object : ProjectListAdapter.ProjectItemClickListener {
                            override fun onItemClick(position: Int) {
                                startActivityForResult(Intent(context, ProjectDetailsActivity::class.java).apply {
                                    searchResult[position].run {
                                        this@SearchDialogFragment.projectId = projectId
                                        putExtra("position", position)
                                        putExtra("projectId", projectId)
                                        putExtra("projectTopic", projectTopic)
                                    }
                                }, RequestCode.OPEN_PROJECT)
                            }
                        }
                    }
                }
            }
        }

        override fun getViewContext(): Context {
            return context!!
        }

        override fun onNetworkFailed() {

        }

        override fun onRequesting() {
            dialog.apply {
                progressBarSearch.isVisible = true
            }

        }

        override fun onRequestFinished() {
            swipeLayoutSearch?.isRefreshing = false
        }
    }
    private val searchImpl = SearchImpl(searchView)
    private val locationView = object : LocationContract.View {
        override fun onLocationPermissionDenied() {
            context?.toast("位置权限被禁止")
        }

        override fun onLocationRequestFailed() {
//            context?.toast("位置信息请求失败")
        }

        override fun onLocationRequestSuccess(location: TencentLocation) {
            dialog?.apply {
                inputTextSearch.hint = getString(R.string.search) + " " + location.city
            }
            this@SearchDialogFragment.location = location
        }

        override fun getViewContext(): Context {
            return context!!
        }

        override fun onRequesting() {

        }

        override fun onRequestFinished() {
        }

    }
    private val locationImpl = LocationImpl(locationView)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!, R.style.bottomDialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.fragment_search_dialog)
            setCanceledOnTouchOutside(true)
            locationImpl.mView = locationView
            searchImpl.mView = searchView
            locationImpl.start()
            locationImpl.alwaysCall = true
            window?.apply {
                attributes.gravity = Gravity.TOP
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
            toolbar_home.navigationIcon = context.getDrawable(R.drawable.ic_arrow_back_black_24dp)
            toolbar_home.setNavigationOnClickListener {
                this@SearchDialogFragment.dismiss()
            }
            toolbar_home.inflateMenu(R.menu.clean_menu)
            toolbar_home.menu.findItem(R.id.searchClear).isVisible = false
            toolbar_home.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.searchClear -> {
                        inputTextSearch.editableText.clear()
                    }
                }
                true
            }
            (cardViewAppBarMain.layoutParams as AppBarLayout.LayoutParams).apply {
                setMargins(context.resources.getDimensionPixelSize(R.dimen.appbar_margin))
                Log.e("top", topMargin.toString())
                Log.e("bottom", bottomMargin.toString())
                Log.e("start", marginStart.toString())
                Log.e("end", marginEnd.toString())
                cardViewAppBarMain.layoutParams = this
            }
            inputTextSearch.isEnabled = true
            inputTextSearch.isClickable = true
            inputTextSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    val city = when {
                        location == null -> null
                        location!!.city == "Unknown" && BuildConfig.DEBUG -> "贺州市"
                        else -> location!!.city
                    }
                    if (!inputTextSearch.editableText.trimEnd().isEmpty())
                    searchImpl.doRequest(searchRequest.apply {
                        searchKey = inputTextSearch.editableText.trimEnd().toString()
                        token = Attributes.loginUserInfo!!.token
                        projectRegion = city
                        projectTopic = searchKey
                        projectContent = null
                    })
                    true
                } else
                    false
            }
            inputTextSearch.addTextChangedListener {
                toolbar_home.menu.findItem(R.id.searchClear).isVisible = !it.isNullOrEmpty()
            }
            swipeLayoutSearch.setOnRefreshListener {
                if (!inputTextSearch.editableText.trimEnd().isEmpty())
                    searchImpl.doRequest(searchRequest)
                else
                    swipeLayoutSearch.isRefreshing = false
            }
            val searchMapString = context.defaultSharedPreferences.getString("searchHistory", null)
            if (searchMapString != null) {
                Log.e("searchHistory", searchMapString)
//                searchMap = searchMapString.fromJson<HashMap<String,Long>>()
                searchHistory = searchMapString.fromJson<ArrayList<Pair<String, Long>>>()
                showSearchHistory(this@apply)
            }
            val layoutParam = cardViewAppBarMain.layoutParams as AppBarLayout.LayoutParams
            layoutParam.topMargin += context.let {
                it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height", "dimen", "android"))
            }
            cardViewAppBarMain.layoutParams = layoutParam
        }

    }

    override fun onResume() {
        dialog.apply {
            locationImpl.doLocationRequest()
            inputTextSearch.requestFocus()
            window?.transucentSystemUI(true)
        }
        super.onResume()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
//            searchImpl.detachView()
//            locationImpl.detachView()
//            context?.defaultSharedPreferences?.edit {
//                putString("searchHistory",searchMap.toJson())
//            }
        }
        super.onHiddenChanged(hidden)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        searchImpl.detachView()
        locationImpl.detachView()
        context?.defaultSharedPreferences?.edit {
            putString("searchHistory", searchHistory.toJson())
        }
        super.onDismiss(dialog)
    }

    private fun showSearchHistory(dialog: Dialog) {
        dialog.apply {
            searchHistory.apply {
                val last = when {
                    size == 0 -> 0
                    size > 10 -> 9
                    else -> size - 1
                }
                GlobalScope.launch(Dispatchers.Main) {
                    chipGroupSearchHistory.isVisible = true
                    chipGroupSearchHistory.removeAllViews()
                    if (size > 0) {
                        for (i in 0..last)
                            chipGroupSearchHistory.addView(
                                    Chip(context).apply {
                                        var tempText = get(i).first
                                        if (tempText.length > 8)
                                            tempText = tempText.substring(0, 7) + "..."
                                        text = tempText
                                        setOnClickListener {
                                            inputTextSearch.editableText.clear()
                                            inputTextSearch.editableText.append(get(i).first)
                                        }
                                    }
                            )
//                        chipGroupSearchHistory.addView(
//                                Chip(context).apply {
//                                    text = "清空搜索历史"
//                                    setOnClickListener {
//
//                                    }
//                                }
//                        )
                        clearSearchHistory.setOnClickListener {
                            searchHistory.clear()
                            chipGroupSearchHistory.removeAllViews()
                            searchHistoryLayout.isVisible = false
                            chipGroupSearchHistory.isVisible = false
                        }
                        searchHistoryLayout.isVisible = true
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.OPEN_PROJECT && resultCode == ResultCode.OK) {
            (searchResultListView.adapter as ProjectListAdapter).apply {
                listProject.remove(listProject.find {
                    it.projectId == this@SearchDialogFragment.projectId
                })
                notifyDataSetChanged()
                (activity as MainActivity).reSearch()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}