package com.example.uber.core.utils.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo


class NetworkStateReceiver(private val networkCallBack: NetworkStateReceiverListener):BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null || intent.extras == null) return

        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(manager.activeNetworkInfo?.state == NetworkInfo.State.CONNECTED) networkCallBack.networkAvailable() else networkCallBack.networkUnavailable()
    }

    interface NetworkStateReceiverListener {
        fun networkAvailable()
        fun networkUnavailable()
    }
}