/*
 * Copyright 2021 Devsung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devsung.androidhelper.support.network.callback

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler

/**
 * Receive events whenever the network changes.
 */
class NetworkCallback(context: Context) {

    /**
     * When you receive an event, you will be able to receive accurate network information by giving a delay.
     * The default is 0.5 seconds.
     */
    var delayMillis = 500L
    private val handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var callback: ConnectivityManager.NetworkCallback
    private val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val request: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    /**
     * Receive events whenever the network changes.
     */
    fun register(onTransport: (transport: Transport) -> Unit) {
        runnable = Runnable { onTransport(getTransport()) }
        fun transport() {
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, delayMillis)
        }
        callback = object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = transport()
            override fun onLost(network: Network) = transport()
        }
        connectivity.registerNetworkCallback(request, callback)
    }

    /**
     * It will no longer receive a network change event.
     */
    fun unregister() {
        connectivity.unregisterNetworkCallback(callback)
    }

    fun getTransport() : Transport {
        val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork) ?: return Transport.DISCONNECT
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> Transport.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Transport.CELL
            else -> Transport.DISCONNECT
        }
    }
}