package com.inahla.clothingsuggester

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.inahla.clothingsuggester.databinding.ActivityMainBinding
import com.inahla.clothingsuggester.model.Interval
import com.inahla.clothingsuggester.network.ApiClient
import com.inahla.clothingsuggester.network.DataManger
import com.inahla.clothingsuggester.network.NetworkUtils
import com.inahla.clothingsuggester.util.PrefUtils
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var client: ApiClient
    private lateinit var utils: NetworkUtils
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude: String = ""
    private var longitude: String = ""
    private var cityName: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        PrefUtils.initPrefs(this)
    }

    private fun init() {
        utils = NetworkUtils()
        client = ApiClient(utils, latitude, longitude)
        client.makeRequest { intervalsList, message ->
            if (message != null) {
                Log.e("error", message)
            } else {
                runOnUiThread {
                    val todayWeather = intervalsList!![0]
                    val sharedPref = PrefUtils.startTime

                    val image = PrefUtils.imageId?.toInt()
                    if (todayWeather.startTime == sharedPref) {
                        if (image != null) todayWeather.clothesImageId = image
                    } else {
                        if (image != null) todayWeather.clothesImageId =
                            DataManger().getClothesImageId(todayWeather.weatherType, image)
                    }
                    cityName = getCityName(this, latitude.toDouble(),longitude.toDouble())
                    setUpBinding(todayWeather)
                    PrefUtils.startTime = todayWeather.startTime
                    PrefUtils.imageId = todayWeather.clothesImageId.toString()
                }
            }

        }


    }

    private fun setup() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
    }

    private fun setUpBinding(todayWeather: Interval) {
        binding.apply {
            textTemperature.text = "${todayWeather.values.temperature.toInt()}°c"
            textHumidityValue.text = "${todayWeather.values.humidity.toString()}%"
            textWindValue.text = "${todayWeather.values.windSpeed.toString()}km/h"
            textMaxTemperatureValue.text = "${todayWeather.values.temperatureMax.toInt()}°c"
            imageClothes.setImageResource(todayWeather.clothesImageId)
            textCity.text = cityName

        }
    }

    private fun getCurrentLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this, ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()
                    } else {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        init()

                    }
                }
            } else {
                Toast.makeText(this, "Turn on location ", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(GPS_PROVIDER) || locationManager.isProviderEnabled(
            NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun getCityName(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context)

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val city = addresses[0].locality
                return city
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    companion object {
        const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

}