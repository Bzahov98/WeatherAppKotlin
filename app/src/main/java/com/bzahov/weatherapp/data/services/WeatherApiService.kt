package com.bzahov.weatherapp.data.services

import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.network.RequestInterceptor
import com.bzahov.weatherapp.data.network.intefaces.ConnectivityInterceptor
import com.bzahov.weatherapp.data.response.current.CurrentWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//TODO extract in string.xml others strings
interface WeatherApiService {
    @GET("current")
    fun getCurrentWeatherAsync(
        @Query("query") location: String ,
        @Query("units") unit: String = "m"
    ) : Deferred<CurrentWeatherResponse>
    companion object{
        private const val TAG = "WeatherApiService"
        private val API_KEY = getAppString(R.string.weather_stack_key)
        private val API_URL = getAppString(R.string.weather_stack_url)
        private val API_KEY_PARAM_NAME = getAppString(R.string.weather_stack_key_param)

        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): WeatherApiService {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(RequestInterceptor.getRequestWithParameter(
                    API_KEY_PARAM_NAME,
                    API_KEY
                ))
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(API_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}