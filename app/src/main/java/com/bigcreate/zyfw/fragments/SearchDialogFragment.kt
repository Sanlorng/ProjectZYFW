package com.bigcreate.zyfw.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.app.DialogCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.mvp.app.LocationContract
import com.bigcreate.zyfw.mvp.app.LocationImpl
import com.bigcreate.zyfw.mvp.base.SearchModel
import com.bigcreate.zyfw.mvp.project.SearchContract
import com.bigcreate.zyfw.mvp.project.SearchImpl
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import com.tencent.map.geolocation.TencentLocation
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.comment_pop.*
import kotlinx.android.synthetic.main.fragment_search_dialog.*
import kotlinx.android.synthetic.main.fragment_setup_info.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class SearchDialogFragment: DialogFragment() {
    var searchKey = ""
    var searchMap = HashMap<String,Long>()
    var location: TencentLocation? = null
    val searchRequest = SearchRequest(Attributes.loginUserInfo!!.token,null,null,null)
    val searchView = object: SearchContract.View{
        override fun onSearchFailed(response: JsonObject) {
            context?.toast(response.toJson())
            progressBarSearch?.isVisible = false
        }

        override fun onSearchFinished(searchResult: List<SearchModel>) {
            dialog.apply {
                GlobalScope.launch {
                    if (searchMap.containsKey(searchKey))
                        searchMap.remove(searchKey)
                    searchMap.put(searchKey,System.currentTimeMillis())
                    if (searchMap.size>10)
                        searchMap.remove(searchMap.keys.first())
                    showSearchHistory(this@apply)
                }
                progressBarSearch.isVisible = false
                searchResult.isEmpty().apply {
                    textSearchEmpty.isVisible = this
                }
                searchResultListView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = ProjectListAdapter(searchResult)
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
    }
    val searchImpl = SearchImpl(searchView)
    val locationView = object : LocationContract.View{
        override fun onLocationPermissionDenied() {
            context?.toast("位置权限被禁止")
        }

        override fun onLocationRequestFailed() {
            context?.toast("位置信息请求失败")
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

        override fun onNetworkFailed() {
            context?.toast("网络连接失败")
        }
    }
    val locationImpl = LocationImpl(locationView)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!,R.style.bottomDialog).apply {
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
                when(it.itemId){
                    R.id.searchClear -> {
                        inputTextSearch.editableText.clear()
                    }
                }
                true
            }
            (cardViewAppBarMain.layoutParams as AppBarLayout.LayoutParams).apply {
                setMargins(context.resources.getDimensionPixelSize(R.dimen.appbar_margin))
                Log.e("top",topMargin.toString())
                Log.e("bottom",bottomMargin.toString())
                Log.e("start",marginStart.toString())
                Log.e("end",marginEnd.toString())
                cardViewAppBarMain.layoutParams = this
            }
            inputTextSearch.isEnabled = true
            inputTextSearch.isClickable = true
            inputTextSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId==EditorInfo.IME_ACTION_SEARCH||(event!=null&&event.keyCode==KeyEvent.KEYCODE_ENTER)){
                    searchImpl.searchProject(searchRequest.apply {
                        searchKey = inputTextSearch.editableText.trimEnd().toString()
                        token = Attributes.loginUserInfo!!.token
                        projectRegion = location?.city
                        projectTopic = searchKey
                        projectContent = projectTopic
                    })
                    true
                }else
                false
            }
            inputTextSearch.addTextChangedListener {
                toolbar_home.menu.findItem(R.id.searchClear).isVisible = !it.isNullOrEmpty()
            }
            val searchMapString = context.defaultSharedPreferences.getString("searchHistory",null)
            if (searchMapString!= null){
                Log.e("searchHistory",searchMapString)
                searchMap = searchMapString.fromJson<HashMap<String,Long>>()
                showSearchHistory(this@apply)
            }
        }

    }

    override fun onResume() {
        dialog.apply {
            locationImpl.doLocationRequest()
            inputTextSearch.requestFocus()
        }
        super.onResume()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden){
//            searchImpl.detachView()
//            locationImpl.detachView()
//            context?.defaultSharedPreferences?.edit {
//                putString("searchHistory",searchMap.toJson())
//            }
        }
        super.onHiddenChanged(hidden)
    }
    override fun dismiss() {

        super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
//        context?.defaultSharedPreferences?.edit {
//            putString("searchHistory",searchMap.toJson())
//        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        searchImpl.detachView()
        locationImpl.detachView()
        context?.defaultSharedPreferences?.edit {
            putString("searchHistory",searchMap.toJson())
        }
        super.onDismiss(dialog)
    }
    private fun showSearchHistory(dialog: Dialog){
        dialog.apply {
            searchMap.toList().apply {
                val last = when{
                    size == 0 -> 0
                    size > 10 -> 9
                    else -> size -1
                }
                GlobalScope.launch(Dispatchers.Main) {
                    chipGroupSearchHistory.isVisible = true
                    chipGroupSearchHistory.removeAllViews()
                    if (size > 0) {
                        for (i in 0..last)
                            chipGroupSearchHistory.addView(
                                    Chip(context).apply {
                                        text = get(i).first
                                        setOnClickListener {
                                            inputTextSearch.editableText.clear()
                                            inputTextSearch.editableText.append(text)
                                        }
                                    }
                            )
                        chipGroupSearchHistory.addView(
                                Chip(context).apply {
                                    text = "清空搜索历史"
                                    setOnClickListener {
                                        searchMap.clear()
                                        chipGroupSearchHistory.removeAllViews()
                                        textSearchHistroy.isVisible = false
                                        chipGroupSearchHistory.isVisible = false
                                    }
                                }
                        )
                        textSearchHistroy.isVisible = true
                    }
                }
            }
        }
    }
}