package com.bigcreate.zyfw.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AuthLoginActivity() {
    private var task: SearchAsyncTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar_search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_search.setNavigationOnClickListener {
            finish()
        }
        keyword_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL)
                attemptSearch()
            true
        }
        keyword_search.requestFocus()
    }

    private fun attemptSearch() {
        if (task != null)
            return
        task = SearchAsyncTask(keyword_search.text.toString())
        task!!.execute(null as Void?)
    }

    @SuppressLint("StaticFieldLeak")
    inner class SearchAsyncTask internal constructor(val string: String) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {

            return false
        }

        override fun onPostExecute(result: Boolean?) {
            if (result!!) {
//                search_result.adapter = ProjectListAdapter(searchResponse!!.content)
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
