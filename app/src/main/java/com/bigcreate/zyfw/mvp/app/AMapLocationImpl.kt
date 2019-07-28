package com.bigcreate.zyfw.mvp.app

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.bigcreate.library.toast
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseView

class AMapLocationImpl(view: View) : BasePresenterImpl<Void, AMapLocation, AMapLocationImpl.View>(view) {
    private var location: AMapLocation? = null
    private var locationClient: AMapLocationClient? = null

    fun startLocation() {
        if (locationClient == null && mView != null) {
            locationClient = AMapLocationClient(mView!!.getViewContext().applicationContext).apply {
                setLocationListener(LocationListener())
                setLocationOption(AMapLocationClientOption().setOnceLocation(mView!!.onceLocation))
                stopLocation()
            }
        }
        locationClient?.startLocation()
    }

    fun stopLocation() {
        locationClient?.stopLocation()
    }

    override fun backgroundRequest(request: Void): AMapLocation? {
        if (location == null) {
            do {

            } while (location == null)
        }
        return location
    }

    override fun afterRequestSuccess(data: AMapLocation?) {
        judgeLocation(data)
    }

    override fun cancelJob() {
        super.cancelJob()
        locationClient?.stopLocation()
        locationClient?.onDestroy()
    }

    private fun judgeLocation(location: AMapLocation?) {
        if (location == null)
            mView?.onRequestFailed(location)
        else
            when (location.errorCode) {
                0 -> mView?.onRequestSuccess(location)
                else -> mView?.getViewContext()?.toast(getErrorText(location.errorCode))
            }
    }

    private fun getErrorText(errorCode: Int): String {   //https://lbs.amap.com/api/android-location-sdk/guide/utilities/errorcode/
        return when (errorCode) {
            1 -> "一些重要参数为空，如context."                               //请对定位传递的参数进行非空判断。
            2 -> "定位失败，由于仅扫描到单个wifi，且没有基站信息。"           //请重新尝试。
            3 -> "获取到的请求参数为空，可能获取过程中出现异常。"             //请对所连接网络进行全面检查，请求可能被篡改。
            4 -> "请求服务器过程中的异常，多为网络情况差，链路不通导致."      //请检查设备网络是否通畅，检查通过接口设置的网络访问超时时间，建议采用默认的30秒。
            5 -> "请求被恶意劫持，定位结果解析失败。"                         //您可以稍后再试，或检查网络链路是否存在异常。
            6 -> "定位服务返回定位失败。"                                     //请获取errorDetail（通过getLocationDetail()方法获取）信息并参考定位常见问题进行解决。
            7 -> "KEY鉴权失败。"                                              //请仔细检查key绑定的sha1值与apk签名sha1值是否对应，或通过高频问题查找相关解决办法。
            8 -> "Android exception常规错误"                                  //请将errorDetail（通过getLocationDetail()方法获取）信息通过工单系统反馈给我们。
            9 -> "定位初始化时出现异常。"                                     //请重新启动定位。
            10 -> "定位客户端启动失败。"                                      //请检查AndroidManifest.xml文件是否配置了APSService定位服务
            11 -> "定位时的基站信息错误。"                                    //请检查是否安装SIM卡，设备很有可能连入了伪基站网络。
            12 -> "缺少定位权限。"                                            //请在设备的设置中开启app的定位权限。
            13 -> "定位失败，由于未获得WIFI列表和基站信息，且GPS当前不可用。" //建议开启设备的WIFI模块，并将设备中插入一张可以正常工作的SIM卡，或者检查GPS是否开启；如果以上都内容都确认无误，请您检查App是否被授予定位权限。
            14 -> "GPS 定位失败，由于设备当前 GPS 状态差。"                   //建议持设备到相对开阔的露天场所再次尝试。
            15 -> "定位结果被模拟导致定位失败"                                //如果您希望位置被模拟，请通过setMockEnable(true);方法开启允许位置模拟
            16 -> "当前POI检索条件、行政区划检索条件下，无可用地理围栏"       //建议调整检索条件后重新尝试，例如调整POI关键字，调整POI类型，调整周边搜区域，调整行政区关键字等。
            17 -> "定位失败，由于手机WIFI功能被关闭同时设置为飞行模式"        //建议手机关闭飞行模式，并打开WIFI开关
            18 -> "定位失败，由于手机没插sim卡且WIFI功能被关闭"               //建议手机插上sim卡，打开WIFI开关
            else -> "未知错误$errorCode"
        }
    }

    inner class LocationListener : AMapLocationListener {
        override fun onLocationChanged(p0: AMapLocation?) {
            location = p0
            p0?.apply {
                Attributes.AppCity = city
            }
            judgeLocation(p0)
        }
    }

    interface View : BaseView {
        val onceLocation: Boolean
        fun onRequestSuccess(location: AMapLocation)
        fun onRequestFailed(location: AMapLocation?)
    }

}