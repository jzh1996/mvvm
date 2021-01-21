package com.jzh.mvvm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv
import com.jzh.mvvm.utils.NetWorkUtil

class NetworkChangeReceiver : BroadcastReceiver() {

    /**
     * 缓存上一次的网络状态
     */
    private var hasNetwork = mmkv.decodeBool(Constant.HAS_NETWORK_KEY, true)

    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = NetWorkUtil.isNetworkConnected(context)
        if (isConnected) {
            if (!hasNetwork) {
                LiveEventBus.get("isConnected").post(isConnected)
            }
        } else {
            //同时开启WIFI和数据的时候，关闭WIFI可能会造成短时间断网，所以这里再判断一次
            if (!NetWorkUtil.isNetworkConnected(context)) {
                LiveEventBus.get("isConnected").post(isConnected)
            }
        }
        hasNetwork = isConnected
    }

}