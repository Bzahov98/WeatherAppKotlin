package com.bzahov.weatherapp.internal

import android.content.Context
import android.net.ConnectivityManager

class OtherUtils {
    companion object{
        fun isOnline(context: Context): Boolean {
            val connectivityManager =context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            // QUESTION: deprecated?
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}