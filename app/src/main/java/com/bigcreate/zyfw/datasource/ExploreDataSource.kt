package com.bigcreate.zyfw.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.ExploreItem
import com.bigcreate.zyfw.models.ExploreListResponse
import com.bigcreate.zyfw.models.PageRequest
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreDataSource(private val networkState: MutableLiveData<NetworkState>) : PageKeyedDataSource<Int, ExploreItem>() {
    private val request = PageRequest(Attributes.token, 1)
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ExploreItem>) {
        tryLoad(params, callback, params.key - 1)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ExploreItem>) {
        tryLoad(params, callback, params.key + 1)
    }

    fun tryLoad(params: LoadParams<Int>, callback: LoadCallback<Int, ExploreItem>, nextKey: Int?) {
        request.token = Attributes.token
        request.pageNum = params.key
        RemoteService.getExploreList(request).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message ?: ""))
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful.not())
                    networkState.postValue(NetworkState.error(response.code().toString()))
                else {
                    if (response.body()?.code == 200) {
                        response.body()?.jsonData.toJson().fromJson<ExploreListResponse>().apply {
                            Attributes.token = newToken
                            callback.onResult(content.list, if (content.hasNextPage) nextKey else null)
                        }
                    }
                }
            }
        })
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ExploreItem>) {
        request.token = Attributes.token
        networkState.postValue(NetworkState.LOADING)
        RemoteService.getExploreList(request).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message ?: ""))
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful.not())
                    networkState.postValue(NetworkState.error(response.code().toString()))
                if (response.isSuccessful) {
                    networkState.postValue(NetworkState.LOADED)
                    if (response.body()?.code == 200) {
                        response.body()?.jsonData.toJson().fromJson<ExploreListResponse>().apply {
                            Attributes.token = newToken
                            callback.onResult(content.list, null, if (content.hasNextPage) 2 else null)
                        }
                    }
                }
            }
        })
    }
}