package com.bigcreate.zyfw.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.edit
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bigcreate.library.*
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.NetworkState
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.datasource.ProjectListDataSource
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_search.*
import java.lang.Exception

class SearchActivity : AuthLoginActivity() {
    private var city = Attributes.AppCity
    private lateinit var networkState: NetworkStateViewModel
    private val searchRequest = SearchRequest("", city, "", null, 1)
    private var searchKey = ""
    private var searchHistory = ArrayList<Pair<String, Long>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        appbarSearchDialog.updatePadding(top = appbarSearchDialog.paddingTop + statusBarHeight)
    }

    override fun afterCheckLoginSuccess() {
        inputSearchBar.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (inputSearchBar.editableText.trimEnd().isNotEmpty())
                    searchRequest.apply {
                        searchKey = inputSearchBar.editableText.trimEnd().toString()
                        token = Attributes.token
                        projectRegion = searchKey
                        projectTopic = null
                        projectContent = null
                        reSearch()
                    }
                true
            } else
                false
        }
        layoutInputSearchBar.setStartIconOnClickListener {
            onBackPressed()
        }
        inputSearchBar.setOnFocusChangeListener { v, hasFocus ->
            showSearchHistory(hasFocus)
        }
        inputSearchBar.requestFocus()
        listSearchResult.itemAnimator = DefaultItemAnimator()
    }

    override fun setContentView() {
        setContentView(R.layout.activity_search)
        actionClearSearchHistory.setOnClickListener {
            searchHistory.clear()
            chipGroupSearchHistory.removeAllViews()
            showSearchHistory(false)
        }
        window.translucentSystemUI(true)
        val searchMapString = defaultSharedPreferences.getString("searchHistory", null)
        if (searchMapString != null) {
            Log.e("searchHistory", searchMapString)
//            searchHistory = searchMapString.fromJson<ArrayList<Pair<String, Long>>>()
//            addAllSearchHistory()
        }
        networkState = ViewModelProvider(this)[NetworkStateViewModel::class.java]
        networkState.state.observe(this, Observer {
            when (it) {
                NetworkState.LOADING -> showProgress(true)
                NetworkState.LOADED -> {
                    showProgress(false)
                    try {
//                        addSearchHistory()
                    }catch (e: Throwable) {
                        e.printStackTrace()
                        throw e
                    }
                }
            }
        })
        Attributes.addCityListener(javaClass.name) {
            city = it
            inputSearchBar.hint = "${getString(R.string.search)} $city"
        }
    }

    private fun reSearch() {
        listSearchResult.adapter = ProjectListAdapter { _, item ->
            startActivity<ProjectDetailsActivity> {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(Uri.parse(String.format(Attributes.authorityProject, item.projectId)), "project/${item.projectTopic}")
                putExtra("projectId", item.projectId)

            }
        }.apply {
            submitList(PagedList.Builder<Int, SearchModel>(
                    ProjectListDataSource(searchRequest,
                            networkState.state),
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            inputSearchBar.clearFocus()
        }
    }

    private fun addAllSearchHistory() {
        chipGroupSearchHistory.removeAllViews()

        searchHistory.forEachIndexed { index, pair ->
            if (index < 10) {
                chipGroupSearchHistory.addView(
                        Chip(this@SearchActivity).apply {
                            id = index
                            var tempText = pair.first
                            if (tempText.length > 8)
                                tempText = tempText.substring(0, 7) + "..."
                            text = tempText
                            setOnClickListener {
                                this@SearchActivity.inputSearchBar.editableText.clear()
                                this@SearchActivity.inputSearchBar.editableText.append(pair.first)
                            }
                            isCloseIconVisible = true
                            setOnCloseIconClickListener {

                            }
                        }
                )
            }else {
                return
            }
        }
    }

    private fun addSearchHistory() {
        searchHistory = ArrayList(searchHistory.filter {
            it.first != searchKey
        })
            if (searchKey.isNotEmpty()) {
                try {
//                    val chip = Chip(chipGroupSearchHistory.context)
//                    chip.id = View.NO_ID
                }catch (e: Exception) {
                    e.printStackTrace()
                }
//                var tempText = searchKey
//                if (tempText.length > 8)
//                    tempText = tempText.substring(0, 7) + "..."
//                //text = tempText
//                chip.text = tempText
//                chip.setOnClickListener {
//                    inputSearchBar.editableText.clear()
//                    inputSearchBar.editableText.append(searchKey)
//                }
//                chip.isCloseIconVisible = true
//                chip.setOnCloseIconClickListener {
//                    searchHistory.removeAt(it.id)
//                    this@SearchActivity.chipGroupSearchHistory.removeViewAt(it.id)
//                    this@SearchActivity.chipGroupSearchHistory.forEachIndexed { index, view ->
//                        if (index >= it.id) {
//                            view.id = index
//                        }
//                    }
//                }
//                chipGroupSearchHistory.addView(chip,0)
//                searchHistory.add(0,searchKey to System.currentTimeMillis())
            }
//        if (searchKey.isNotEmpty()) {
//            searchHistory.add(0, searchKey to System.currentTimeMillis())
//        }
            if (this@SearchActivity.chipGroupSearchHistory.childCount > 10) {
                this@SearchActivity.chipGroupSearchHistory.removeViewAt(10)
                searchHistory.removeAt(10)
            }
            this@SearchActivity.chipGroupSearchHistory.forEachIndexed { index, view ->
                view.id = index
            }

    }

    private fun showProgress(value: Boolean) {

    }

    private fun showSearchHistory(show: Boolean) {
        chipGroupSearchHistory.isVisible = show
        actionClearSearchHistory.isVisible = show
    }

    override fun onBackPressed() {
//        if (inputSearchBar.hasFocus()) {
////            showSearchHistory(false)
//            inputSearchBar.clearFocus()
//            getSystemService(InputMethodManager::class.java).hideSoftInputFromWindow(inputSearchBar.windowToken, 0)
//        } else {
//            super.onBackPressed()
//        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        defaultSharedPreferences.edit {
            putString("searchHistory", searchHistory.toJson())
        }
        Attributes.removeCityListener(javaClass.name)
    }
}
