package com.bigcreate.zyfw.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.WebKit
import com.bigcreate.library.postRequest
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.base.myApplication
import com.bigcreate.zyfw.models.SearchRequire
import com.bigcreate.zyfw.models.SearchResponse
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    var task:SearchAsyncTask ?= null
    var searchResponse : SearchResponse ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar_search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_search.setNavigationOnClickListener {
            finish()
        }
        keyword_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL)
                attemptSearch()
            true
        }
        keyword_search.requestFocus()
//        keyword_search.callOnClick()
    }

    private fun attemptSearch(){
        if (task != null)
            return
        task = SearchAsyncTask(keyword_search.text.toString())
        task!!.execute(null as Void?)
    }
    @SuppressLint("StaticFieldLeak")
    inner class SearchAsyncTask internal constructor(val string: String):AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg params: Void?): Boolean {
           return try {
               myApplication?.run {
                   val response = WebKit.okClient.postRequest(WebInterface.SEARCH_URL, WebInterface.TYPE_JSON, WebKit.gson.toJson(SearchRequire(null, string,"桂林")))
                   val responseString = response?.string()
                   Log.d("is client","yes")
                   responseString?.run {
                       Log.d("response",this)
                   }
                   searchResponse = WebKit.gson.fromJson<SearchResponse>(responseString, SearchResponse::class.java)
               }
               searchResponse != null && searchResponse?.stateCode?.compareTo(200) == 0
           }catch (e:Exception){
               Log.d("error","when search request")
               false
           }
        }

        override fun onPostExecute(result: Boolean?) {
            if (result!!) {
                search_result.adapter = ProjectListAdapter(searchResponse!!.content)
                search_result.layoutManager = LinearLayoutManager(this@SearchActivity)
            }
            super.onPostExecute(result)
        }
    }

    override fun onResume() {
        window.transucentSystemUI(true)
        super.onResume()
    }
}
