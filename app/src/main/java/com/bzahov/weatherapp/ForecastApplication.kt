package com.bzahov.weatherapp

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.bzahov.weatherapp.data.db.ForecastDatabase
import com.bzahov.weatherapp.data.network.ConnectivityInterceptorImpl
import com.bzahov.weatherapp.data.network.CurrentWeatherNetworkDataSourceImpl
import com.bzahov.weatherapp.data.network.FutureWeatherNetworkDataSourceImpl
import com.bzahov.weatherapp.data.network.intefaces.ConnectivityInterceptor
import com.bzahov.weatherapp.data.network.intefaces.CurrentWeatherNetworkDataSource
import com.bzahov.weatherapp.data.network.intefaces.FutureWeatherNetworkDataSource
import com.bzahov.weatherapp.data.provider.LocationProviderImpl
import com.bzahov.weatherapp.data.provider.UnitProviderImpl
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.CurrentForecastRepositoryImpl
import com.bzahov.weatherapp.data.repo.FutureForecastRepositoryImpl
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.data.services.OpenWeatherApiService
import com.bzahov.weatherapp.data.services.WeatherApiService
import com.bzahov.weatherapp.ui.settings.SettingsFragmentViewModelFactory
import com.bzahov.weatherapp.ui.weather.current.CurrentWeatherViewModelFactory
import com.bzahov.weatherapp.ui.weather.future.detail.FutureDetailWeatherViewModelFactory
import com.bzahov.weatherapp.ui.weather.future.list.FutureListWeatherViewModelFactory
import com.bzahov.weatherapp.ui.weather.oneday.OneDayWeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import java.time.LocalDateTime

class ForecastApplication : Application(), KodeinAware {


    override val kodein = Kodein.lazy {
        import(androidXModule(this@ForecastApplication))

        bind() from singleton { ForecastDatabase(instance()) }

        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().currentLocationDao() }
        bind() from singleton { instance<ForecastDatabase>().forecastDao() } // for forecast

        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        // bind different weather api services
        bind() from singleton {
            WeatherApiService(
                instance()
            )
        } // for current
        bind() from singleton { OpenWeatherApiService(instance()) } // for forecast

        // bind different data sources for each api service
        bind<CurrentWeatherNetworkDataSource>() with singleton { CurrentWeatherNetworkDataSourceImpl(instance()) }
        bind<FutureWeatherNetworkDataSource>() with singleton { FutureWeatherNetworkDataSourceImpl(instance()) }
        // bind app repository
        bind<CurrentForecastRepository>() with singleton { CurrentForecastRepositoryImpl(instance(), instance(),instance(),instance(),instance()) }
        bind<FutureForecastRepository>() with singleton {
            FutureForecastRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        // bind all providers
        bind<UnitProvider>() with singleton{UnitProviderImpl(instance())}
        bind<LocationProvider>() with singleton{ LocationProviderImpl(instance(),instance()) }

        //bind location providers
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }

        //bind all fragment's view models
        bind() from provider { CurrentWeatherViewModelFactory(instance(),instance(),instance())}
        bind() from provider { SettingsFragmentViewModelFactory(instance(),instance(),instance()) }
        bind() from provider { FutureListWeatherViewModelFactory(instance(),instance(),instance()) }
        bind() from factory { detailDate: LocalDateTime -> FutureDetailWeatherViewModelFactory(detailDate,instance(),instance(),instance()) }
        bind() from provider { OneDayWeatherViewModelFactory(instance(),instance(),instance()) }
        //bind() from factory { startDate: LocalDateTime, endDate: LocalDateTime -> OneDayWeatherViewModelFactory(startDate,endDate,instance(),instance(),instance()) }

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