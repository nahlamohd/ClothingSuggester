package com.inahla.clothingsuggester.model

import androidx.annotation.DrawableRes


class Interval (
    val startTime: String,
    val values: Values,
    val weatherType: DayWeatherType,
    @DrawableRes var clothesImageId: Int,
    //@DrawableRes val WeatherImageId: Int
    )

