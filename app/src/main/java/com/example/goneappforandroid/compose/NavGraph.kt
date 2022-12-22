package com.example.goneappforandroid.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.goneappforandroid.TasksViewModel
import com.example.goneappforandroid.compose.bottomappbar.CustomBottomAppBarItems
import com.example.goneappforandroid.compose.tasks.TasksScreen
import com.example.goneappforandroid.data.Task

@Composable
fun NavGraph(
    navHostController: NavHostController,
    topBarTitle: MutableState<String>,
    tasksList: State<List<Task>>,
    tasksViewModel: TasksViewModel,
){
    NavHost(navController = navHostController, startDestination = CustomBottomAppBarItems.tasks.route) {
        composable(route = CustomBottomAppBarItems.tasks.route){
            topBarTitle.value = "Today"
            TasksScreen(tasksList = tasksList, tasksViewModel = tasksViewModel)
        }
        composable(route = CustomBottomAppBarItems.settings.route){
            topBarTitle.value = "Settings"
        }
        composable(route = "overview"){

        }
    }
}