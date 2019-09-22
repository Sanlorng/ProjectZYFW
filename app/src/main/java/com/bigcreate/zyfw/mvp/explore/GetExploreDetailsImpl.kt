package com.bigcreate.zyfw.mvp.explore

import com.bigcreate.library.fromJson
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.ExploreItem
import com.bigcreate.zyfw.models.ExploreRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class GetExploreDetailsImpl(view: View) : BasePresenterImpl<ExploreRequest,JsonObject,GetExploreDetailsImpl.View>(view) {
    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.apply {
            data?.run {
                if (code == 200) {
                    Attributes.token = newTokenFromData
                    val datalist = jsonData.getAsJsonArray("content")
                    if (datalist.size() > 0) {
                        onGetExploreDetailsSuccess(datalist[0].toString().fromJson())
                        return
                    }
                }
                onGetExploreDetailsFailed(this)
            }
        }
    }

    override fun backgroundRequest(request: ExploreRequest): JsonObject? {
        return RemoteService.getExploreDetails(request).execute().body()
    }
    interface View:BaseNetworkView {
        fun onGetExploreDetailsSuccess(item: ExploreItem)
        fun onGetExploreDetailsFailed(jsonObject: JsonObject)
    }
}