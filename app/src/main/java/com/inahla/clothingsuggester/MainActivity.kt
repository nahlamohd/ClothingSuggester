package com.inahla.clothingsuggester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import com.inahla.clothingsuggester.databinding.ActivityMainBinding
import com.inahla.clothingsuggester.model.Interval
import com.inahla.clothingsuggester.network.ApiClient
import com.inahla.clothingsuggester.network.DataManger
import com.inahla.clothingsuggester.network.NetworkUtils
import com.inahla.clothingsuggester.util.PrefsUtil
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var client: ApiClient
    private lateinit var utils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        client.makeRequest { intervalsList, message ->
            if (message != null) {
                Log.e("error", message)
            } else {
                runOnUiThread {
                    val todayWeather = intervalsList!![0]
                    setUpBinding(todayWeather)
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
            textTemperature.text = "${todayWeather.values.temperature}°c"
            textHumidity.text = todayWeather.values.humidity.toString()
            textWindSpeed.text = todayWeather.values.windSpeed.toString()
            textMaxTemperature.text = "${todayWeather.values.temperatureMax}°c"
        }
    }
}