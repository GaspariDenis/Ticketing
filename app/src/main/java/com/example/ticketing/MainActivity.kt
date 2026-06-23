package com.example.ticketing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ticketing.auth.Auth
import com.example.ticketing.auth.AuthenticationScreen
import com.example.ticketing.dashboard.DashboardScreen
import com.example.ticketing.dashboard.Home
import com.example.ticketing.dashboardTickets.DashboardTickets
import com.example.ticketing.dashboardTickets.DashboardTicketsScreen
import com.example.ticketing.projectDetails.ProjectDetails
import com.example.ticketing.projectDetails.ProjectDetailsScreen
import com.example.ticketing.register.Register
import com.example.ticketing.register.RegisterScreen
import com.example.ticketing.ui.theme.TicketingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicketingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = Auth) {
                        composable<Auth>{
                            AuthenticationScreen(
                                modifier = Modifier.padding(innerPadding),
                                nav = navController
                            )
                        }
                        composable<Home> {
                            DashboardScreen(
                                modifier = Modifier.padding(innerPadding),
                                nav = navController
                            )
                        }
                        composable<ProjectDetails>{ fallback ->
                            ProjectDetailsScreen(
                                modifier = Modifier.padding(innerPadding),
                                projectId = fallback.toRoute<ProjectDetails>().projectId,
                                nav = navController
                            )
                        }
                        composable<Register>{
                            RegisterScreen(
                                nav = navController
                            )
                        }
                        composable<DashboardTickets> {
                            DashboardTicketsScreen(
                                nav = navController
                            )
                        }
                    }
                }
            }
        }
    }
}