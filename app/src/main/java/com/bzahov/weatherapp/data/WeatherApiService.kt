package com.bzahov.weatherapp.data

import android.util.Log
import com.bzahov.weatherapp.data.network.ConnectivityInterceptorImpl
import com.bzahov.weatherapp.data.response.CurrentWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "WeatherApiService"
const val API_KEY = "d008d274c9b7d028454d802a2f80a75a"
const val API_URL = "http://api.weatherstack.com/"
interface WeatherApiService {
    @GET("current")
    fun getCurrentWeather(
        @Query("query") location: String ,
        //@Query("language") languageCode: String = "en",
        @Query("unit") unit: String = "m"
    ) : Deferred<CurrentWeatherResponse>
    companion object{
        operator fun invoke(
            connectivityInterceptorImpl: ConnectivityInterceptorImpl
        ): WeatherApiService {
            val requestInterceptor = Interceptor{
                val url = it.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("access_key", API_KEY)
                    .build()

                val request = it.request()
                    .newBuilder()
                    .url(url)
                    .build()

                Log.d(TAG,"Invoke() of getCurrentWeather with url: $url, and request $request")

                return@Interceptor it.proceed(request)
            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptorImpl)
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