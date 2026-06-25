package com.example.ticketing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
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
import com.example.ticketing.projectSetting.ProjectSetting
import com.example.ticketing.projectSetting.ProjectSettingScreen
import com.example.ticketing.register.Register
import com.example.ticketing.register.RegisterScreen
import com.example.ticketing.ticketChange.TicketCreation
import com.example.ticketing.ticketChange.TicketChangeScreen
import com.example.ticketing.ticketDetails.TicketDetails
import com.example.ticketing.ticketDetails.TicketDetailsScreen
import com.example.ticketing.ui.theme.TicketingTheme
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.MemberListCustomNavType
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.ProjectCustomNavType
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.TicketCustomNavType
import com.example.ticketing.vo.UserTag
import com.example.ticketing.vo.UserTagCustomNavType
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.typeOf


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
                                userTag = fallback.toRoute<ProjectDetails>().userTag,
                                nav = navController
                            )
                        }
                        composable<Register>{
                            RegisterScreen(
                                nav = navController
                            )
                        }
                        composable<DashboardTickets>(
                            typeMap = mapOf(typeOf<Project>() to ProjectCustomNavType,
                                            typeOf<List<Member>>() to MemberListCustomNavType,
                                            typeOf<UserTag>() to UserTagCustomNavType
                            )
                        ) {fallback ->
                            DashboardTicketsScreen(
                                project = fallback.toRoute<DashboardTickets>().project,
                                modifier = Modifier.padding(innerPadding),
                                nav = navController,
                                youTag = fallback.toRoute<DashboardTickets>().youTag,
                                projectMembers = fallback.toRoute<DashboardTickets>().projectMembers
                            )
                        }
                        composable<TicketCreation>(
                            typeMap = mapOf(typeOf<Ticket>() to TicketCustomNavType,
                                            typeOf<List<Member>>() to MemberListCustomNavType,
                                            typeOf<UserTag>() to UserTagCustomNavType
                            )
                        ){ fallback ->
                            TicketChangeScreen(
                                modifier = Modifier.padding(innerPadding),
                                nav = navController,
                                ticket = fallback.toRoute<TicketCreation>().ticket,
                                members = fallback.toRoute<TicketCreation>().members,
                                youTag = fallback.toRoute<TicketCreation>().userTag
                            )
                        }
                        composable<ProjectSetting>(
                            typeMap = mapOf(typeOf<Project>() to ProjectCustomNavType)
                        ) {fallback ->
                            ProjectSettingScreen(
                                modifier = Modifier.padding(innerPadding),
                                nav = navController,
                                project = fallback.toRoute<ProjectSetting>().project
                            )
                        }
                        composable<TicketDetails>(
                            typeMap = mapOf(typeOf<List<Member>>() to MemberListCustomNavType,
                                typeOf<UserTag>() to UserTagCustomNavType
                            )
                        ) { fallback ->
                            TicketDetailsScreen(
                                modifier = Modifier.padding(innerPadding),
                                nav = navController,
                                youTag = fallback.toRoute<TicketDetails>().youTag,
                                projectMembers = fallback.toRoute<TicketDetails>().listOfMember,
                                projectId = fallback.toRoute<TicketDetails>().projectId,
                                ticketId = fallback.toRoute<TicketDetails>().ticketId
                            )
                        }
                    }
                }
            }
        }
    }
}