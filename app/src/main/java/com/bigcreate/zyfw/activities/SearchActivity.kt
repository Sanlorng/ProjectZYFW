package com.bigcreate.zyfw.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
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
        window.transucentSystemUI(true)
        keyword_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL)
                attemptSearch()
            true
        }
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
                   val response = okHttpClient.postRequest(WebInterface.SEARCH_URL, WebInterface.TYPE_JSON, gson.toJson(SearchRequire("桂林", string)))
                   val responseString = response?.string()
                   searchResponse = gson.fromJson<SearchResponse>(responseString, SearchResponse::class.java)
               }
               searchResponse != null && searchResponse?.stateCode?.compareTo("200") == 0
           }catch (e:Exception){
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
}
