package com.inahla.clothingsuggester.model

import androidx.annotation.DrawableRes


class Interval (
    val values: Values,
    val weatherType: DayWeatherType,
    @DrawableRes val clothesImageId: Int
    )

