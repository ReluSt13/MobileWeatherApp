package com.relustatescu.weatherapp.domain.weather

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.relustatescu.weatherapp.presentation.MainActivity
import kotlin.math.roundToInt

class WeatherNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun showNotification(weatherData: WeatherData, locationName: String) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${weatherData.temperatureC}°C in $locationName. Probability of rain: ${weatherData.precipitationProbability.roundToInt()}%  - Shared from notification - MobileWeatherApp by Relu Stătescu")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, "Share via")
        val sharePendingIntent = PendingIntent.getActivity(
            context,
            2,
            shareIntent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(context, WEATHER_CHANNEL_ID)
            .setSmallIcon(weatherData.weatherType.iconRes)
            .setContentTitle("${weatherData.temperatureC}°C in $locationName")
            .setContentText("${weatherData.weatherType.weatherDesc} - Probability of rain: ${weatherData.precipitationProbability.roundToInt()}%")
            .setContentIntent(activityPendingIntent)
            .addAction(
                weatherData.weatherType.iconRes,
                "Share",
                sharePendingIntent
            )
            .build()

        notificationManager.notify(1, notification)

    }

    companion object {
        const val WEATHER_CHANNEL_ID = "weather_channel"
    }
}