package com.inahla.clothingsuggester.model

import androidx.annotation.DrawableRes
data class Interval (
    val startTime: String,
    val values: WeatherValues,
    val weatherType: DayWeatherType,
    @DrawableRes var clothesImageId: Int,
    )
data class WeatherValues(
    val humidity: Double,
    val temperature: Double,
    val temperatureMax: Double,
    val windSpeed: Double
)

