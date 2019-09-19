package com.bigcreate.zyfw.datasource

import androidx.paging.PageKeyedDataSource
import com.bigcreate.library.fromJson
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.jsonContentFromData
import com.bigcreate.zyfw.callback.enqueue
import com.bigcreate.zyfw.models.ExploreItem
import com.bigcreate.zyfw.models.ListDataContent
import com.bigcreate.zyfw.models.ProvinceProject
import com.google.gson.JsonObject

class ProvinceProjectDataSource(private val area:String):PageKeyedDataSource<Int,ProvinceProject>() {
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ProvinceProject>) {
        RemoteService.getNearByProject(area,params.key).enqueue {
            response {
                body()?.apply {
                    if (code == 200) {
                        val content = jsonContentFromData.toString().fromJson<ListDataContent<ProvinceProject>>()
                        callback.onResult(content.list,if (content.hasNextPage) content.nextPage else null)
                    }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ProvinceProject>) {
        RemoteService.getNearByProject(area,params.key).enqueue {
            response {
                body()?.apply {
                    if (code == 200) {
                        val content = jsonContentFromData.toString().fromJson<ListDataContent<ProvinceProject>>()
                        callback.onResult(content.list,if (content.hasPreviousPage) content.prePage else null)
                    }
                }
            }
        }
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ProvinceProject>) {
        RemoteService.getNearByProject(area,1).enqueue {
            response {
                body()?.apply {
                    if (code == 200) {
                        val content = jsonContentFromData.toString().fromJson<ListDataContent<ProvinceProject>>()
                        callback.onResult(content.list,null ,if (content.hasNextPage) content.nextPage else null)
                    }
                }
            }
        }
    }

}