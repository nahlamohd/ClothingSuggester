package com.inahla.clothingsuggester.network

import com.inahla.clothingsuggester.R
import com.inahla.clothingsuggester.model.DayWeatherType
import com.inahla.clothingsuggester.model.Values

class DataManger {

    private fun getClothesList(dayWeatherType: DayWeatherType): List<Int> {
        return when(dayWeatherType) {
            DayWeatherType.COLD -> {
                listOf(
                    R.drawable.winter_1,
                    R.drawable.winter_2,
                    R.drawable.winter_3,
                    R.drawable.winter_5
                )}
                else -> listOf(
                R.drawable.summer_1,
                R.drawable.summer_2,
                R.drawable.summer_3,
                R.drawable.summer_4,
                R.drawable.summer_5,
                R.drawable.summer_6
                )
        }
    }

    fun getClothesImageId(dayWeatherType: DayWeatherType) :Int{
        return when (dayWeatherType) {
            DayWeatherType.COLD -> {
                getClothesList(dayWeatherType).random()
            }
            else -> {
                getClothesList(dayWeatherType).random()
            }
        }
    }

    fun getDatWeatherType(valves : Values): DayWeatherType{
        return when {
            valves.temperature <= 20.0 -> {
                DayWeatherType.COLD
            }
            else -> {
                DayWeatherType.HOT
            }
        }
    }
}
