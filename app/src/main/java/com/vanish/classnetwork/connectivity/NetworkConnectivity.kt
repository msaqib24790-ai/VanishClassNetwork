package com.vanish.classnetwork.connectivity

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@SuppressLint("MissingPermission")
private fun hasValidatedInternet(manager: ConnectivityManager): Boolean {
    val network = manager.activeNetwork ?: return false
    val capabilities = manager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

@SuppressLint("MissingPermission")
@Composable
fun rememberInternetConnectivity(): Boolean {
    val context = LocalContext.current

    val manager = remember(context) {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    var internetAvailable by remember(manager) {
        mutableStateOf(hasValidatedInternet(manager))
    }

    DisposableEffect(manager) {
        var registered = false

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                internetAvailable = hasValidatedInternet(manager)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                internetAvailable =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }

            override fun onLost(network: Network) {
                internetAvailable = hasValidatedInternet(manager)
            }
        }

        try {
            manager.registerDefaultNetworkCallback(
                callback,
                Handler(Looper.getMainLooper())
            )
            registered = true
            internetAvailable = hasValidatedInternet(manager)
        } catch (exception: Exception) {
            internetAvailable = false
        }

        onDispose {
            if (registered) {
                try {
                    manager.unregisterNetworkCallback(callback)
                } catch (_: Exception) {
                    // Callback was already unregistered.
                }
            }
        }
    }

    return internetAvailable
}
