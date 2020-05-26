package com.bzahov.weatherapp.data.services

import android.util.Log
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.network.RequestInterceptor
import com.bzahov.weatherapp.data.network.intefaces.ConnectivityInterceptor
import com.bzahov.weatherapp.data.response.future.ForecastWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//TODO extract in string.xml others strings
interface OpenWeatherApiService {
    @GET("data/2.5/forecast")
    fun getForecastWeatherAsync(
        @Query("q") location: String? ,
        @Query("lat") lat: Double? ,
        @Query("lon") lon: Double? ,
        @Query("units") unit: String = getAppString(R.string.default_open_unit_system)
    ) : Deferred<ForecastWeatherResponse>
    companion object{
        private const val TAG = "OpenWeatherApiService"
        private val API_KEY = getAppString(R.string.weather_open_key)
        private val API_URL = getAppString(R.string.weather_open_url)
        private val API_KEY_PARAM_NAME = getAppString(R.string.weather_open_key_param)

        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): OpenWeatherApiService {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(RequestInterceptor.getRequestWithParameter(API_KEY_PARAM_NAME, API_KEY))
                .addInterceptor(connectivityInterceptor)
                .build()


            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(API_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherApiService::class.java)
        }
        suspend fun test(create:OpenWeatherApiService){
            val list = create.getForecastWeatherAsync(getAppString(R.string.default_location),null,null).await().toString()
            val list2 = create.getForecastWeatherAsync(null,42.2,23.2).await().toString()
            Log.e("SSS", "RRR $list RRR $list2")
        }
    }
}