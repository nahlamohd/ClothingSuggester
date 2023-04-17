package com.inahla.clothingsuggester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.inahla.clothingsuggester.databinding.ActivityMainBinding
import com.inahla.clothingsuggester.model.Interval
import com.inahla.clothingsuggester.network.ApiClient
import com.inahla.clothingsuggester.network.DataManger
import com.inahla.clothingsuggester.network.NetworkUtils
import com.inahla.clothingsuggester.util.PrefUtils

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var client: ApiClient
    private lateinit var utils: NetworkUtils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        PrefUtils.initPrefs(this)
        client.makeRequest { intervalsList, message ->
            if (message != null) {
                Log.e("error", message)
            } else {
                runOnUiThread {
                    val todayWeather = intervalsList!![0]
                    val sharedPref = PrefUtils.startTime

                    val image = PrefUtils.imageId?.toInt()
                    if(todayWeather.startTime == sharedPref) {
                        if(image != null) todayWeather.clothesImageId = image
                    }
                    else {
                        if(image != null) todayWeather.clothesImageId =
                            DataManger().getClothesImageId(todayWeather.weatherType,image)
                    }
                    setUpBinding(todayWeather)
                    PrefUtils.startTime= todayWeather.startTime
                    PrefUtils.imageId = todayWeather.clothesImageId.toString()

                }
            }

        }

    }

    private fun init(){
        utils = NetworkUtils()
        client = ApiClient(utils)
    }

    private fun setUpBinding(todayWeather: Interval) {
        binding.apply {
            textTemperature.text = "${todayWeather.values.temperature.toInt()} °c"
            textHumidityValue.text = "${todayWeather.values.humidity.toString()} %"
            textWindValue.text = "${todayWeather.values.windSpeed.toString()} km/h"
            textMaxTemperatureValue.text = "${todayWeather.values.temperatureMax.toInt()}°c"
            imageClothes.setImageResource(todayWeather.clothesImageId)

        }
    }
}