package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var btnGetWeather: Button
    private lateinit var tvWeatherDescription: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvName: TextView
    private lateinit var fltBtn: FloatingActionButton

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    private val baseUrl = "https://api.openweathermap.org/data/2.5/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApiService: WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetWeather = findViewById(R.id.btnGetWeather)
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvName = findViewById(R.id.tvName)
        fltBtn = findViewById(R.id.fltBtn)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        sharedPreferences = getSharedPreferences("WeatherData", Context.MODE_PRIVATE)

        btnGetWeather.setOnClickListener {
            requestLocationPermission()
        }

        fltBtn.setOnClickListener {
            changeActivity()
        }
    }

    private fun changeActivity(){
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        makeApiCall(latitude, longitude)
                    }
                }
        } else {
            Toast.makeText(this, "Please Enable Location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeApiCall(latitude: Double, longitude: Double) {
        val apiKey = "602fbfd19f2d2a900868e9a5893b436d"

        val call: Call<WeatherResponse> =
            weatherApiService.getWeather(latitude, longitude, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    val weatherInfo = weatherResponse?.weather?.get(0)
                    val description = weatherInfo?.description
                    val temperature = weatherResponse?.main?.temperature
                    val humidity = weatherResponse?.main?.humidity
                    val name = weatherResponse?.name

                    // Save data to SharedPreferences
                    saveWeatherData(description, temperature, humidity, name)

                    // Update UI with retrieved data
                    updateWeatherUI(description, temperature, humidity, name)

                } else {
                    val errorMessage = "API Error: ${response.code()}"
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()

                    // Retrieve the last known data from SharedPreferences
                    val previousData = getDataFromSharedPreferences()

                    // Update UI with the previous data if available
                    updateWeatherUI(
                        previousData.description,
                        previousData.temperature,
                        previousData.humidity,
                        previousData.name
                    )
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                val errorMessage = "Network Error: ${t.localizedMessage}"
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()

                // Retrieve the last data from SharedPreferences
                val previousData = getDataFromSharedPreferences()

                // Update UI with the previous data if available
                updateWeatherUI(
                    previousData.description,
                    previousData.temperature,
                    previousData.humidity,
                    previousData.name
                )
            }
        })
    }

    private fun saveWeatherData(
        description: String?,
        temperature: Double?,
        humidity: Double?,
        name: String?
    ) {
        val editor = sharedPreferences.edit()
        editor.putString("description", description)
        editor.putString("temperature", temperature?.toString())
        editor.putString("humidity", humidity?.toString())
        editor.putString("name", name)
        editor.apply()
    }

    private fun getDataFromSharedPreferences(): WeatherData {
        val description = sharedPreferences.getString("description", null)
        val temperature = sharedPreferences.getString("temperature", null)?.toDouble()
        val humidity = sharedPreferences.getString("humidity", null)?.toDouble()
        val name = sharedPreferences.getString("name", null)

        return WeatherData(description, temperature, humidity, name)
    }

    private fun updateWeatherUI(
        description: String?,
        temperature: Double?,
        humidity: Double?,
        name: String?
    ) {
        tvWeatherDescription.text = description
        tvTemperature.text = "${temperature ?: "-"} F"
        tvHumidity.text = "Humidity : ${humidity ?: "-"}"
        tvName.text = name
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

data class WeatherData(
    val description: String?,
    val temperature: Double?,
    val humidity: Double?,
    val name: String?
)


