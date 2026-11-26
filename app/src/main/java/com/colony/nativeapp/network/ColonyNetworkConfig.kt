package com.colony.nativeapp.network

import com.colony.nativeapp.BuildConfig

object ColonyNetworkConfig {
    val apiBaseUrl: String
        get() = BuildConfig.API_BASE_URL

    val websocketUrl: String
        get() = BuildConfig.WEBSOCKET_URL
}
