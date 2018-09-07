package com.bigcreate.library

import android.annotation.TargetApi
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat.getSystemService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Inet4Address
import java.net.NetworkInterface


val Application.serverAdress:String
    get() = "192.168.199.1"
val Context.ipAddress: String?
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected){
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            when(networkCapabilities.connectType){
                NetworkCapabilities.TRANSPORT_CELLULAR -> {
                    for (networkInterface in NetworkInterface.getNetworkInterfaces()) {
                        for (item in networkInterface.inetAddresses)
                            if (item.isLoopbackAddress.not() && item is Inet4Address)
                                return item.hostAddress
                    }
                }
                NetworkCapabilities.TRANSPORT_WIFI -> {
                    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val intIP = wifiManager.connectionInfo.ipAddress
                    return ((intIP and 0xff).toString() + "." + (intIP shr 8 and 0xff) + "."
                            + (intIP shr 16 and 0xff) + "." + (intIP shr 24 and 0xff))

                }
            }
        }
        return null
    }
val Context.isConnected:Boolean
get() {
    val info = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
    return info!= null && info.isConnected
}
val NetworkCapabilities.connectType:Int?
get() {
    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
        return NetworkCapabilities.TRANSPORT_WIFI
    if (hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH))
        return NetworkCapabilities.TRANSPORT_BLUETOOTH
    if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        return NetworkCapabilities.TRANSPORT_CELLULAR
    return null
}
