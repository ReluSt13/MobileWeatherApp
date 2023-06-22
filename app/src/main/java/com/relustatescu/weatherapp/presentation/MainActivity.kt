package com.relustatescu.weatherapp.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.identity.Identity
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.relustatescu.weatherapp.domain.weather.WeatherNotificationService
import com.relustatescu.weatherapp.presentation.sign_in.GoogleAuthUiClient
import com.relustatescu.weatherapp.presentation.sign_in.SignInScreen
import com.relustatescu.weatherapp.presentation.sign_in.SignInViewModel
import com.relustatescu.weatherapp.presentation.ui.theme.DarkBlue
import com.relustatescu.weatherapp.presentation.ui.theme.DeepBlue
import com.relustatescu.weatherapp.presentation.ui.theme.Shapes
import com.relustatescu.weatherapp.presentation.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()
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

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            weatherViewModel.loadWeatherInfo()
        }
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
        createNotificationChannel()
        setContent {
            WeatherAppTheme {
                val searchText by weatherViewModel.searchText.collectAsState()
                val cities by weatherViewModel.cities.collectAsState()
                val isSearching by weatherViewModel.isSearching.collectAsState()
                val isSearchFocused = remember { mutableStateOf(false) }
                val focusRequester = remember { FocusRequester() }
                val focusManager = LocalFocusManager.current
                val navController = rememberNavController()
                val notificationService = WeatherNotificationService(applicationContext)
                LaunchedEffect(Unit) {
                    delay(10000)
                    notificationService.showNotification(weatherViewModel.state.weatherInfo?.currentWeatherData!!, weatherViewModel.state.locationInfo?.location!!)
                }

                NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
                    composable(route = Screen.LoginScreen.route) {
                        val signInViewModel = viewModel<SignInViewModel>()
                        val state by signInViewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null) {
                                navController.navigate(Screen.MainScreen.route)
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if(result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        signInViewModel.onSignInResult(signInResult)
                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if(state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate(Screen.MainScreen.route)
                                signInViewModel.resetState()
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }
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
                                    onValueChange = weatherViewModel::onSearchTextChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
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
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val intent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_TEXT, "${googleAuthUiClient.getSignedInUser()?.username} wants you to know that: ${weatherViewModel.state.weatherInfo?.currentWeatherData?.temperatureC}°C in ${weatherViewModel.state.locationInfo?.location} - Probability of rain ${weatherViewModel.state.weatherInfo?.currentWeatherData?.precipitationProbability?.roundToInt()}% - MobileWeatherApp by Relu Stătescu")
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
                                            if(googleAuthUiClient.getSignedInUser()?.profilePictureUrl != null) {
                                                AsyncImage(
                                                    model = googleAuthUiClient.getSignedInUser()?.profilePictureUrl,
                                                    contentDescription = "Profile picture",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .pointerInput(Unit) {
                                                            detectTapGestures(
                                                                onLongPress = {
                                                                    lifecycleScope.launch {
                                                                        googleAuthUiClient.signOut()
                                                                        Toast.makeText(
                                                                            applicationContext,
                                                                            "Signed out",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()

                                                                        navController.navigate(Screen.LoginScreen.route)
                                                                    }
                                                                }
                                                            )
                                                        }
                                                )
                                            }
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
                                                                weatherViewModel.loadWeatherOfSelected(
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
                                        WeatherCard(state = weatherViewModel.state, backgroundColor = DeepBlue)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        WeatherForecast(state = weatherViewModel.state)
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
                                                        .size(46.dp)
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
                            if(weatherViewModel.state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            weatherViewModel.state.error?.let { error ->
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
                                    weatherViewModel.getWeatherFromMap(it)
                                }
                            ) {
                                Marker(
                                    position = weatherViewModel.state.coords!!,
                                    title = "${weatherViewModel.state.locationInfo?.location} (${weatherViewModel.state.coords!!.latitude}, ${weatherViewModel.state.coords!!.longitude})",
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