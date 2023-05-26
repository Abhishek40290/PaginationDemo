package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
@SerializedName("weather")
val weather: List<WeatherInfo>,
@SerializedName("main")
val main: WeatherMainInfo,
@SerializedName("name")
val name: String
)

data class WeatherInfo(
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String
)

data class WeatherMainInfo(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int
)
