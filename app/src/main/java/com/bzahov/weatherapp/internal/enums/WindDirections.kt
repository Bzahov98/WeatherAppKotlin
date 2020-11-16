package com.bzahov.weatherapp.internal.enums

import com.bzahov.weatherapp.R

// TODO: ADD INFO IN ABOUT <a target="_blank" href="https://icons8.com/icons/set/north-direction">North Direction</a>, <a target="_blank" href="https://icons8.com/icons/set/north-east">North East</a> and other icons by <a target="_blank" href="https://icons8.com">Icons8</a>
enum class WindDirections(
	val description: String,
	val shortName: String,
	val range: ClosedFloatingPointRange<Double>,
	val image: Int
) {
    NORTH("N North", "N" ,(0.0..45.0), R.drawable.wind_north_red50px), // TODO: ADD 360 degree to north
    NORTH_EAST("NE North-East", "NE" ,45.1..90.0, R.drawable.wind_north_east_red),
    EAST("E East", "E", 90.1..135.0,R.drawable.wind_east_red50px),
    SOUTH_EAST("SE South-East", "SE", 135.1..180.0,R.drawable.wind_south_east_red50),
    SOUTH("S South", "S", 180.1..225.0,R.drawable.wind_south_red50px),
    SOUTH_WEST("SW South-West", "SW", 225.1..270.0,R.drawable.wind_south_west_red50),
    WEST("W West", "W",270.1..315.0,R.drawable.wind_west_red50px),
    NORTH_WEST("NW North-West", "NW",315.1..359.9,R.drawable.wind_north_west_red),
    NOT_FOUND ("", "-",-1.1..-1.0,0);

    companion object {
        fun getDescriptionStringByDouble(windDirection: Double): String {
            return getAllWindDirectionByDouble(windDirection).description
        }
        fun getShortDescriptionStringByDouble(windDirection: Double): String {
            return getAllWindDirectionByDouble(windDirection).shortName
        }

        fun getAllWindDirectionByDouble(windDirectionAngle: Double): WindDirections {
            return when (windDirectionAngle) {
                360.0 -> NORTH //  catching my special case for 360 and return NORTH
                in NORTH.range-> NORTH //  0  and 45 and 360
                in NORTH_EAST.range -> NORTH_EAST //   45 and 90
                in EAST.range -> EAST //   90 and 135
                in SOUTH_EAST.range -> SOUTH_EAST //  135 and 180
                in SOUTH.range -> SOUTH //  180  and 225
                in SOUTH_WEST.range -> SOUTH_WEST //  225 and 270
                in WEST.range -> WEST //  270.1  and 315
                in NORTH_WEST.range -> NORTH_WEST //  315.1 and 359.9
                else -> NOT_FOUND // when angle is not between 0 and 359.9
            }
        }

        @Deprecated("Add more Directions", ReplaceWith(
            "getAllWindDirectionByDouble (old getWindDirectionByDouble) "
        ))
        fun getWindDirectionByDouble(windDirectionAngle: Double): WindDirections {
            return when (windDirectionAngle) {
                in NORTH_EAST.range -> NORTH_EAST //   0 and 90
                in SOUTH_EAST.range -> SOUTH_EAST //  90 and 180
                in SOUTH_WEST.range -> SOUTH_WEST //  180 and 269
                in NORTH_WEST.range -> NORTH_WEST //  270 and 359
                else -> NOT_FOUND
            }
        }
    }
}
