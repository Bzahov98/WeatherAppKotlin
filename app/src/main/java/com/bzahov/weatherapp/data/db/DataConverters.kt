package com.bzahov.weatherapp.data.db

import androidx.room.TypeConverter
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
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

    @TypeConverter
    fun storedStringToWeatherDescription(data: String?): List<WeatherDetails>? {
        val gson = Gson()
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<WeatherDetails?>?>() {}.type
        return gson.fromJson<List<WeatherDetails>>(data, listType)
    }

    @TypeConverter
    fun weatherDescriptionToStoredString(myObjects: List<WeatherDetails>?): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }
}
