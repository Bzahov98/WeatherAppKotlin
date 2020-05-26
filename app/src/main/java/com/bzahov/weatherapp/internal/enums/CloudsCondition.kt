package com.bzahov.weatherapp.internal.enums

enum class CloudsCondition( valueID: Int, description: String, iconDay: String,iconNight: String ) {
    CLEAR_SKY       (800,"Clear sky "      ,"02d","02n"),
    FEW_CLOUDS      (801,"Few clouds: 11-25% "      ,"02d","02n"),
    SCATTERED_CLOUDS(802,"Scattered clouds: 25-50% ","03d","03n"),
    BROKEN_CLOUDS   (803,"Broken clouds: 51-84% "   ,"04d","04n"),
    OVERCAST_CLOUDS (804,"Overcast clouds: 85-100% ","04d","04n");

}
