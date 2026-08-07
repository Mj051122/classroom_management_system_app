package com.myapplication.panthraa.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.myapplication.panthraa.model.ConnectivityStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun currentStatus(): ConnectivityStatus {
        val network = connectivityManager.activeNetwork ?: return ConnectivityStatus.Offline
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return ConnectivityStatus.Connecting

        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        return when {
            hasInternet && isValidated -> ConnectivityStatus.Online
            hasInternet -> ConnectivityStatus.Connecting
            else -> ConnectivityStatus.Offline
        }
    }

    fun isOnline(): Boolean = currentStatus() == ConnectivityStatus.Online

    fun statusFlow(): Flow<ConnectivityStatus> = callbackFlow {
        trySend(currentStatus())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(currentStatus())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                trySend(currentStatus())
            }

            override fun onLost(network: Network) {
                trySend(ConnectivityStatus.Connecting)
                trySend(currentStatus())
            }

            override fun onUnavailable() {
                trySend(ConnectivityStatus.Offline)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
