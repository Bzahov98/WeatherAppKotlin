package com.bzahov.weatherapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bzahov.weatherapp.data.db.ForecastDatabase
import com.bzahov.weatherapp.data.network.ConnectivityInterceptorImpl
import com.bzahov.weatherapp.data.network.CurrentWeatherNetworkDataSourceImpl
import com.bzahov.weatherapp.data.network.FutureWeatherNetworkDataSourceImpl
import com.bzahov.weatherapp.data.network.intefaces.ConnectivityInterceptor
import com.bzahov.weatherapp.data.network.intefaces.CurrentWeatherNetworkDataSource
import com.bzahov.weatherapp.data.network.intefaces.FutureWeatherNetworkDataSource
import com.bzahov.weatherapp.data.provider.InternetProviderImpl
import com.bzahov.weatherapp.data.provider.LocationProviderImpl
import com.bzahov.weatherapp.data.provider.UnitProviderImpl
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.CurrentForecastRepositoryImpl
import com.bzahov.weatherapp.data.repo.FutureForecastRepositoryImpl
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.data.services.OpenWeatherApiService
import com.bzahov.weatherapp.data.services.WeatherApiService
import com.bzahov.weatherapp.ui.fragments.SettingsFragmentViewModelFactory
import com.bzahov.weatherapp.ui.remoteviews.notifications.AlarmReceiver
import com.bzahov.weatherapp.ui.remoteviews.notifications.cancelNotifications
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

        bind<ConnectivityInterceptor>() with singleton {
            ConnectivityInterceptorImpl(
                instance(),
                instance()
            )
        }
        // bind different weather api services
        bind() from singleton {
            WeatherApiService(
                instance()
            )
        } // for current
        bind() from singleton { OpenWeatherApiService(instance()) } // for forecast

        // bind different data sources for each api service
        bind<CurrentWeatherNetworkDataSource>() with singleton {
            CurrentWeatherNetworkDataSourceImpl(
                instance()
            )
        }
        bind<FutureWeatherNetworkDataSource>() with singleton {
            FutureWeatherNetworkDataSourceImpl(
                instance()
            )
        }
        // bind app repository
        bind<CurrentForecastRepository>() with singleton {
            CurrentForecastRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind<FutureForecastRepository>() with singleton {
            FutureForecastRepositoryImpl(
                instance(), instance(), instance(), instance(), instance()
            )
        }

        // bind all providers
        bind<UnitProvider>() with singleton { UnitProviderImpl(instance()) }
        bind<LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }
        bind<InternetProvider>() with singleton { InternetProviderImpl(instance()) }

        //bind location providers
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }

        //bind all fragment's view models
        bind() from provider {
            CurrentWeatherViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind() from provider {
            SettingsFragmentViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        bind() from provider {

        }
        bind() from provider {
            FutureListWeatherViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind() from factory { detailDate: LocalDateTime ->
            FutureDetailWeatherViewModelFactory(
                detailDate,
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind() from provider {
            OneDayWeatherViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        //bind() from factory { startDate: LocalDateTime, endDate: LocalDateTime -> OneDayWeatherViewModelFactory(startDate,endDate,instance(),instance(),instance()) }
    }
    private lateinit var app: ForecastApplication
    private lateinit var alarmManager: AlarmManager

    //    private var prefs =
//        app.getSharedPreferences("com.example.android.eggtimernotifications", Context.MODE_PRIVATE)
    private lateinit var notifyIntent: Intent

    private val second: Long = 1_000L
    private val REQUEST_CODE = 0
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        resourcesNew = resources


        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        //testAlarmNotification()
    }

    fun testAlarmNotification() {
        val app: ForecastApplication = this
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notifyIntent = Intent(app, AlarmReceiver::class.java)

        val notificationManager =
            ContextCompat.getSystemService(
                app,
                NotificationManager::class.java
            ) as NotificationManager

        var notifyPendingIntent = PendingIntent.getBroadcast(
            this,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationManager.cancelNotifications()
        val triggerTime = SystemClock.elapsedRealtime() + second * 10

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_DEFAULT
            )// TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
//            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            notificationChannel.description = getString(R.string.notification_channel_description)

            val notificationManager = applicationContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
        // TODO: Step 1.6 END create a channel
    }

    companion object {
        var resourcesNew: Resources? = null
        fun getAppResources(): Resources? {
            return resourcesNew
        }

        fun getAppString(id: Int): String {
            return resourcesNew!!.getString(id)
        }
    }

}