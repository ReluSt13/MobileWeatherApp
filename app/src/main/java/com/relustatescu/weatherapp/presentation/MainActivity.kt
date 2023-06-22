package com.relustatescu.weatherapp.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.relustatescu.weatherapp.domain.weather.WeatherNotificationService
import com.relustatescu.weatherapp.presentation.ui.theme.DarkBlue
import com.relustatescu.weatherapp.presentation.ui.theme.DeepBlue
import com.relustatescu.weatherapp.presentation.ui.theme.Shapes
import com.relustatescu.weatherapp.presentation.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                WeatherNotificationService.WEATHER_CHANNEL_ID,
                "Weather",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for the weather share notification"
            val notificationmanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationmanager.createNotificationChannel(channel)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.loadWeatherInfo()
        }
        createNotificationChannel()
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
        setContent {
            WeatherAppTheme {
                val searchText by viewModel.searchText.collectAsState()
                val cities by viewModel.cities.collectAsState()
                val isSearching by viewModel.isSearching.collectAsState()
                val isSearchFocused = remember { mutableStateOf(false) }
                val focusRequester = remember { FocusRequester() }
                val focusManager = LocalFocusManager.current
                val navController = rememberNavController()
                val notificationService = WeatherNotificationService(applicationContext)
                LaunchedEffect(Unit) {
                    delay(10000)
                    notificationService.showNotification(viewModel.state.weatherInfo?.currentWeatherData!!, viewModel.state.locationInfo?.location!!)
                }

                NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
                    composable(route = Screen.MainScreen.route) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ){
                            Column (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DarkBlue)
                            ) {
                                TextField(
                                    value = searchText,
                                    onValueChange = viewModel::onSearchTextChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .background(color = DeepBlue, shape = Shapes.medium)
                                        .focusRequester(focusRequester)
                                        .onFocusChanged { isSearchFocused.value = it.isFocused },
                                    placeholder = {
                                        if (!isSearchFocused.value){
                                            Text(text = "Search city", fontSize = 18.sp)
                                        }
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        textColor =  Color.White,
                                        placeholderColor = Color.White
                                    ),
                                    textStyle = TextStyle(
                                        fontSize = 18.sp
                                    ),
                                    maxLines = 1,
                                    leadingIcon = {
                                        if (isSearchFocused.value) {
                                            IconButton(
                                                onClick = {
                                                    focusRequester.requestFocus()
                                                    focusManager.clearFocus()
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowBack,
                                                    contentDescription = "Back",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, "${viewModel.state.weatherInfo?.currentWeatherData?.temperatureC}°C in ${viewModel.state.locationInfo?.location} - MobileWeatherApp by Relu Stătescu")
                                                    type = "text/plain"
                                                }
                                                val shareIntent = Intent.createChooser(intent, "Share via")
                                                startActivity(shareIntent)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share Weather",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                )
                                if(isSearching) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                } else {
                                    Log.d("MAIN-BS", cities.toString())
                                    AnimatedVisibility(visible = isSearchFocused.value) {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ){
                                            items(cities!!) { city ->
                                                Box(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = city.description,
                                                        modifier = Modifier
                                                            .clickable {
                                                                viewModel.loadWeatherOfSelected(
                                                                    city.place_id
                                                                ); focusRequester.requestFocus(); focusManager.clearFocus()
                                                            }
                                                            .fillMaxWidth()
                                                            .padding(16.dp),
                                                        color = Color.White
                                                    )
                                                }

                                            }
                                        }
                                    }
                                }
                                AnimatedVisibility(visible = !isSearchFocused.value) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        WeatherCard(state = viewModel.state, backgroundColor = DeepBlue)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        WeatherForecast(state = viewModel.state)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            IconButton(
                                                onClick = { navController.navigate(Screen.MapScreen.route) }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Map,
                                                    contentDescription = "Map Icon",
                                                    tint = Color.White,
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .background(DeepBlue, shape = CircleShape)
                                                        .border(
                                                            1.dp,
                                                            Color.White,
                                                            shape = CircleShape
                                                        )
                                                        .padding(6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if(viewModel.state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            viewModel.state.error?.let { error ->
                                Text(
                                    text = error,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    composable(route = Screen.MapScreen.route){
                        Box(modifier = Modifier.fillMaxSize()) {
                            GoogleMap(
                                modifier = Modifier
                                    .fillMaxSize(),
                                onMapClick = {
                                    viewModel.getWeatherFromMap(it)
                                }
                            ) {
                                Marker(
                                    position = viewModel.state.coords!!,
                                    title = "${viewModel.state.locationInfo?.location} (${viewModel.state.coords!!.latitude}, ${viewModel.state.coords!!.longitude})",
                                    onClick = {
                                        it.showInfoWindow()
                                        true
                                    }
                                )
                            }
                            IconButton(
                                onClick = {
                                    navController.navigate(Screen.MainScreen.route)
                                },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = DeepBlue
                                )
                            }


                        }
                    }
                }

            }
        }
    }
}