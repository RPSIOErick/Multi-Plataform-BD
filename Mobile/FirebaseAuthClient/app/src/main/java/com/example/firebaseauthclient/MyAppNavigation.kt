package com.example.firebaseauthclient

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firebaseauthclient.ui.screens.HomePage
import com.example.firebaseauthclient.ui.screens.LoginPage
import com.example.firebaseauthclient.ui.screens.SignupPage
import com.example.firebaseauthclient.ui.screens.services.ServiceDetailsScreen
import com.example.firebaseauthclient.ui.screens.services.ServiceFormScreen
import com.example.firebaseauthclient.ui.screens.services.ServicesListScreen

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("services") {
            ServicesListScreen(modifier, navController, viewModel())
        }

        composable("service_details/{serviceId}", arguments = listOf(navArgument("serviceId") { type = NavType.StringType })) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            Log.d("ServiceDetailsScreen", "Received serviceId: $serviceId")
            ServiceDetailsScreen(serviceId, navController)
        }

        composable("service_form") {
            ServiceFormScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("service_form/{serviceId}") { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            ServiceFormScreen(
                navController = navController,
                authViewModel = authViewModel,
                serviceId = serviceId
            )
        }

    })
}
