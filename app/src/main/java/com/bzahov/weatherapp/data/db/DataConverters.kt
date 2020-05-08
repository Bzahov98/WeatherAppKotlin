package com.bzahov.weatherapp.data.db

import androidx.room.TypeConverter
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

//List<WeatherDescription>
class DateConverters {
    @TypeConverter
    fun storedStringToMyObjects(data: String?): List<String>? {
        val gson = Gson()
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson<List<String>>(data, listType)
    }

    @TypeConverter
    fun myObjectsToStoredString(myObjects: List<String>?): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    companion object {
        @TypeConverter
        @JvmStatic
        fun dateToString(dateTime: LocalDate?) = dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE)

        //
        @TypeConverter
        @JvmStatic
        fun stringToDate(str: String?) = str?.let {
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        }

    }
    //

    //
    @TypeConverter
    fun storedStringToWeatherDescrp(data: String?): List<WeatherDetails>? {
        val gson = Gson()
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<WeatherDetails?>?>() {}.type
        return gson.fromJson<List<WeatherDetails>>(data, listType)
    }

    @TypeConverter
    fun weatherDescrpToStoredString(myObjects: List<WeatherDetails>?): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun stringToDateTime(string: String?) = string?.let {
        val dtFormatter =
            DateTimeFormatter.ofPattern(ForecastApplication.getAppString(R.string.date_formatter_pattern))
        //  val dateString = dateTime.format(dtFormatter)
        LocalDateTime.parse(it, dtFormatter)
    }

    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime?): String? {
        val dtFormatter =
            DateTimeFormatter.ofPattern(ForecastApplication.getAppString(R.string.date_formatter_pattern))
        return dateTime?.format(dtFormatter)

//    @TypeConverter
//    fun convertToDatabaseColumn(locDate: LocalDate?): Date? {
//        return locDate?.let { Date.from(locDate.atStartOfDay(systemDefault()).toInstant()) }
//    }
////    @TypeConverter
//    fun convertTosDatabaseColumn(locDate: LocalDate?): Date? {
//        return locDate?.let { Date.from(locDate.atStartOfDay(systemDefault()).toInstant()) }
//    }

//    @TypeConverter
//    fun convertToEntityAttribufte(sqlDate: String?): LocalDate? {
//        val defaultZoneId = systemDefault()
//        //val instant = sqlDate.
//        val gson = Gson()
//        val listType: Type = object : TypeToken<LocalDate?>() {}.type
//        return gson.fromJson(sqlDate, listType)
//        //return instant?.atZone(defaultZoneId)?.toLocalDate()
//    }
        //List<ForecastWeatherEntry>


//    @TypeConverter
//    fun <T> genericToStoredStringFe(myObjects: List<T>?): String? {
//        val gson = Gson()
//        return gson.toJson(myObjects)
//    }
//
//    @TypeConverter
//    fun<T> storedStringToGeneric(data: String?): List<T>? {
//        val gson = Gson()
//        if (data == null) {
//            return Collections.emptyList()
//        }
//        val listType: Type = object : TypeToken<List<WeatherDetails?>?>() {}.type
//        return gson.fromJson<List<T>>(data, listType)
//    }


    }
}