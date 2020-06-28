package com.bzahov.weatherapp.data.provider.interfaces

interface InternetProvider{
    var isNetworkConnected : Boolean
    fun registerNetworkCallback()
}