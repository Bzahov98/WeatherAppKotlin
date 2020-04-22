package com.bzahov.weatherapp

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.bzahov.weatherapp.data.WeatherApiService
import com.bzahov.weatherapp.data.db.ForecastDatabase
import com.bzahov.weatherapp.data.network.ConnectivityInterceptorImpl
import com.bzahov.weatherapp.data.network.WeatherNetworkDataSourceImpl
import com.bzahov.weatherapp.data.network.intefaces.ConnectivityInterceptor
import com.bzahov.weatherapp.data.network.intefaces.WeatherNetworkDataSource
import com.bzahov.weatherapp.data.provider.LocationProviderImpl
import com.bzahov.weatherapp.data.provider.UnitProviderImpl
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.ForecastRepository
import com.bzahov.weatherapp.data.repo.ForecastRepositoryImpl
import com.bzahov.weatherapp.ui.settings.SettingsFragmentViewModelFactory
import com.bzahov.weatherapp.ui.weather.current.CurrentWeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
class ForecastApplication : Application(), KodeinAware {


    override val kodein = Kodein.lazy {
        import(androidXModule(this@ForecastApplication))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().currentLocationDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { WeatherApiService(instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance(),instance(),instance(),instance()) }
        bind<UnitProvider>() with singleton{UnitProviderImpl(instance())}

        //bind() from provider { MainActivity(instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }

        bind<LocationProvider>() with singleton{ LocationProviderImpl(instance(),instance()) }
        bind() from provider { CurrentWeatherViewModelFactory(instance(),instance(),instance())}
        bind() from provider { SettingsFragmentViewModelFactory(instance(),instance()) }

    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false)
        resourcesNew = resources
    }


    companion object{
        var resourcesNew: Resources? = null
        fun getAppResources(): Resources? {
            return resourcesNew
        }

        fun getAppString(id: Int): String {
            return resourcesNew!!.getString(id)
        }
    }

}