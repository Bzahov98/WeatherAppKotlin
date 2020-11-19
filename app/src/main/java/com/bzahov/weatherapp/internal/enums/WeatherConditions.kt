package com.bzahov.weatherapp.internal.enums

import com.bzahov.weatherapp.R


enum class WeatherConditions(
	val code: Array<Int>,
	val description: String,
	val dayAnimationRes: Int = 0,
	val nightAnimationRes: Int = 0
) {
	CLOUDS(
		arrayOf(122, 119),
		"Clouds",
		R.raw.weather_clouds_256,
		R.raw.weather_clouds_partly_night
	),
	PARTLY_CLOUDS(
		arrayOf(116),
		"Partly Clouds",
		R.raw.weather_clouds_partly,
		R.raw.weather_clouds_partly_night
	),
	SNOW(
		arrayOf(395, 392, 371, 368, 338, 335, 332, 329, 326, 323, 230, 227, 179),
		"Snow",
		R.raw.weather_snow,
		R.raw.weather_snow_night
	),
	RAIN(
		arrayOf(389, 386, 359, 356, 353, 314, 311, 308, 305, 302, 299, 296, 293, 176, 200),
		"Snow",
		R.raw.weather_rain_clouds_sun,
		R.raw.weather_rain_night
	),
	SUNNY_CLEAR(
		arrayOf(113),
		"Clear/Sunny",
		R.raw.weather_sun,
		R.raw.weather_moon_clear
	),
	SLEET(
		arrayOf(320, 362, 365, 317, 182),
		"Sleet",
		R.raw.weather_snow_clouds_sun,
		R.raw.weather_rain_night
	),
	DRIZZLE(
		arrayOf(284, 281, 266, 263, 185),
		"Drizzle",
		R.raw.weather_snow_clouds_sun,
		R.raw.weather_rain_night
	),
	FOG(
		arrayOf(143, 260, 248, 230),
		"Fog",
		R.raw.weather_mist,
		R.raw.weather_mist
	),
	NOT_FOUND(arrayOf(0), "NOT FOUND", R.raw.lf30_editor_9cym405e, R.raw.not_found_404);

	companion object {
		fun getWeatherByCode(code: Int): WeatherConditions {
			return WeatherConditions.values()
				.find {
					it.code.contains(code)
				} ?: NOT_FOUND
		}
	}
}