package com.bzahov.weatherapp.data.network

import android.content.Context
import com.bzahov.weatherapp.data.network.intefaces.ConnectivityInterceptor
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.internal.exceptions.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptorImpl(
    context: Context,
    private val internetProvider: InternetProvider
) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!internetProvider.isNetworkConnected)
            throw NoConnectivityException()
        return chain.proceed(chain.request())
    }
}