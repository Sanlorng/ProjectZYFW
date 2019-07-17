package com.bigcreate.zyfw.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.Comment
import com.bigcreate.zyfw.models.CommentListRequest
import com.bigcreate.zyfw.models.ProjectListCommentResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommentListDataSource(private val commentRequest: CommentListRequest,
                            private val networkState: MutableLiveData<NetworkState>)
    : PageKeyedDataSource<Int,Comment>() {

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Comment>) {
        commentRequest.pageNum = params.key
        commentRequest.token = Attributes.token
        RemoteService.getProjectComments(commentRequest).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message?:""))
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful.not())
                    networkState.postValue(NetworkState.error(response.code().toString()))
                response.body()?.apply {
                    if (code == 200) {
                        jsonData.toJson().fromJson<ProjectListCommentResponse>().apply {
                            Attributes.token = newToken
                            callback.onResult(content.list,if (content.hasNextPage) params.key+1  else null)
                        }
                    }
                }

            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Comment>) {
        commentRequest.pageNum = params.key
        commentRequest.token = Attributes.token
        RemoteService.getProjectComments(commentRequest).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message?:""))
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful.not())
                    networkState.postValue(NetworkState.error(response.code().toString()))
                response.body()?.apply {
                    if (code == 200) {
                        jsonData.toJson().fromJson<ProjectListCommentResponse>().apply {
                            Attributes.token = newToken
                            callback.onResult(content.list,if (content.hasPreviousPage) params.key-1  else null)
                        }
                    }
                }

            }
        })
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Comment>) {
        commentRequest.pageNum = 1
        commentRequest.token = Attributes.token
        networkState.postValue(NetworkState.LOADING)
        RemoteService.getProjectComments(commentRequest).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message?:""))
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful.not())
                    networkState.postValue(NetworkState.error(response.code().toString()))
                response.body()?.apply {
                    if (code == 200) {
                        networkState.postValue(NetworkState.LOADED)
                        jsonData.toJson().fromJson<ProjectListCommentResponse>().apply {
                            Attributes.token = newToken
                            callback.onResult(content.list,null,if (content.hasNextPage) 2 else null)
                        }
                    }
                }

            }
        })
    }
}