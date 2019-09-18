package com.bigcreate.zyfw.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.callback.enqueue
import com.bigcreate.zyfw.models.ExploreCommentInfoRequest
import com.bigcreate.zyfw.models.ExploreCommentItem
import com.bigcreate.zyfw.models.ListDataContent

class DynamicCommentListDataSource(
        private val networkState: MutableLiveData<NetworkState>,
        private val dynamicId: Int): PageKeyedDataSource<Int,ExploreCommentItem>() {

    private fun tryload(params: LoadParams<Int>, callback: LoadCallback<Int, ExploreCommentItem>,before:Boolean = false) {
        networkState.postValue(NetworkState.LOADING)
        RemoteService.getExploreComment(ExploreCommentInfoRequest(Attributes.token,dynamicId,params.key)).enqueue {
            response {
                if (isSuccessful.not()) {
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    body()?.apply {
                        if (code == 200) {
                            networkState.postValue(NetworkState.LOADED)
                            val data = jsonData
                            Attributes.token = data.newToken
                            data.jsonContent.toJson().fromJson<ListDataContent<ExploreCommentItem>>().apply {
                                if (before) {
                                    callback.onResult(list,if (hasPreviousPage) prePage else null )
                                }else {
                                    callback.onResult(list, if (hasNextPage) nextPage else null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ExploreCommentItem>) {
        tryload(params,callback,false)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ExploreCommentItem>) {
        tryload(params,callback,true)
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ExploreCommentItem>) {
        networkState.postValue(NetworkState.LOADING)
        RemoteService.getExploreComment(ExploreCommentInfoRequest(
                Attributes.token,
                dynamicId,
                1
        )).enqueue {
            error {

            }

            response {
                if (isSuccessful.not()) {
                    networkState.postValue(NetworkState.error(code().toString()))
                } else {
                    body()?.apply {
                        if (code == 200) {
                            networkState.postValue(NetworkState.LOADED)
                            val data = jsonData
                            Attributes.token = data.newToken
                            data.jsonContent.toJson().fromJson<ListDataContent<ExploreCommentItem>>().apply {
                                Log.e("datasource",size.toString())
                                callback.onResult(list,null, if (hasNextPage) nextPage else null)
                            }
                        }
                    }
                }
            }
        }
    }
}