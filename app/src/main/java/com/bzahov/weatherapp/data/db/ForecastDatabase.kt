package com.bzahov.weatherapp.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bzahov.weatherapp.data.db.dao.CurrentWeatherDao
import com.bzahov.weatherapp.data.db.dao.ForecastDao
import com.bzahov.weatherapp.data.db.dao.WeatherLocationDao
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails

@Database(
    entities = [CurrentWeatherEntry::class, WeatherLocation::class, FutureDayData::class, WeatherDetails::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateConverters::class, LocalDateConverter::class)
abstract class ForecastDatabase : RoomDatabase() {
    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun currentLocationDao(): WeatherLocationDao
    abstract fun forecastDao(): ForecastDao

    companion object {
        @Volatile
        private var instance: ForecastDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ForecastDatabase::class.java, "forecast.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
