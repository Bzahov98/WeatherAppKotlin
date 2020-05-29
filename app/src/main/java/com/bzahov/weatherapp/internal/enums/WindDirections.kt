package com.bzahov.weatherapp.internal.enums

import com.bzahov.weatherapp.R

// TODO: ADD INFO IN ABOUT <a target="_blank" href="https://icons8.com/icons/set/north-direction">North Direction</a>, <a target="_blank" href="https://icons8.com/icons/set/north-east">North East</a> and other icons by <a target="_blank" href="https://icons8.com">Icons8</a>
enum class WindDirections(val description: String,val shortName : String, val range: ClosedFloatingPointRange<Double>,val image: Int) {
    NORTH_EAST("NE North-East", "NE" ,0.0..90.0, R.drawable.wind_north_east_red),
    SOUTH_EAST("SE South-East", "SE", 90.1..180.0,R.drawable.wind_south_east_red50),
    SOUTH_WEST("SW South-West", "SW", 180.1..270.0,R.drawable.wind_south_west_red50),
    NORTH_WEST("NW North-West", "NW",270.1..359.9,R.drawable.wind_north_west_red),
    NOT_FOUND ("", "-",-1.1..-1.0,0);

    companion object {
        fun getDescriptionStringByDouble(windDirection: Double): String {
            return getWindDirectionByDouble(windDirection).description
        }
        fun getShortDescriptionStringByDouble(windDirection: Double): String {
            return getWindDirectionByDouble(windDirection).shortName
        }

        fun getWindDirectionByDouble(windDirection: Double): WindDirections {
            return when (windDirection) {
                in NORTH_EAST.range -> NORTH_EAST //   0 and 90
                in SOUTH_EAST.range -> SOUTH_EAST //  90 and 180
                in SOUTH_WEST.range -> SOUTH_WEST //  90 and 180
                in NORTH_WEST.range -> NORTH_WEST //  90 and 180
                else -> NOT_FOUND
            }
        }
    }
}
