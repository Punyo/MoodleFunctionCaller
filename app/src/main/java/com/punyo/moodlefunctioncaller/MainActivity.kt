package com.punyo.moodlefunctioncaller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.punyo.moodlefunctioncaller.login.LoginWebView
import com.punyo.moodlefunctioncaller.main.MainScreen
import com.punyo.moodlefunctioncaller.ui.theme.MoodleFunctionCallerTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WebView.setWebContentsDebuggingEnabled(true)
        intent.scheme?.let { Log.d("MainActivity", it) }
        setContent {
            MoodleFunctionCallerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = ScreenDestination.LoginWebView.name
                    ) {
                        composable(ScreenDestination.LoginWebView.name) {
                            LoginWebView(
                                modifier = Modifier.padding(innerPadding),
                                onTokenReceived = {
                                    navController.navigate(
                                        ScreenDestination.Main.name + "/$it"
                                    )
                                },
                            )
                        }
                        composable(
                            route = "${ScreenDestination.Main.name}/{token}",
                            arguments = listOf(
                                navArgument("token") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            val token = it.arguments?.getString("token")
                            MainScreen(
                                modifier = Modifier.padding(innerPadding),
                                token = token ?: ""
                            )
                        }
                    }
                }
            }
        }
    }
}


enum class ScreenDestination {
    LoginWebView,
    Main
}