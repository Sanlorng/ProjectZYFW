package com.bigcreate.zyfw.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.models.SearchResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectListDataSource(private val searchRequest: SearchRequest, private val networkState: MutableLiveData<NetworkState>) : PageKeyedDataSource<Int, SearchModel>() {

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, SearchModel>) {
        searchRequest.token = Attributes.token
        searchRequest.pageNum = params.key
        RemoteService.searchProjectByBlur(searchRequest).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message ?: ""))
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                response.body()?.apply {
                    if (code == 200) {
                        jsonData.toJson().fromJson<SearchResponse>().apply {
                            Attributes.token = newToken
                            callback.onResult(content.list, if (content.hasNextPage) params.key + 1 else null)
                        }
                    }
                }
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, SearchModel>) {
        searchRequest.token = Attributes.token
        searchRequest.pageNum = params.key
        RemoteService.searchProjectByBlur(searchRequest).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message ?: ""))
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                response.body()?.apply {
                    if (response.isSuccessful.not())
                        networkState.postValue(NetworkState.error(response.code().toString()))
                    if (code == 200) {
                        jsonData.toJson().fromJson<SearchResponse>().apply {
                            Attributes.token = newToken
                            callback.onResult(content.list, if (content.hasPreviousPage) params.key - 1 else null)
                        }
                    }
                }
            }
        })
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, SearchModel>) {
        searchRequest.token = Attributes.token
        networkState.postValue(NetworkState.LOADING)
        RemoteService.searchProjectByBlur(searchRequest).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message ?: ""))
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful.not())
                    networkState.postValue(NetworkState.error(response.code().toString()))
                response.body()?.apply {
                    if (code == 200) {
                        networkState.postValue(NetworkState.LOADED)
                        jsonData.toJson().fromJson<SearchResponse>().apply {
                            Attributes.token = newToken
                            callback.onResult(content.list, null, if (content.hasNextPage) 2 else null)
                        }
                    }
                }
            }
        })
    }
}