

package com.example.goneappforandroid.compose

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.preference.PreferenceManager
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.goneappforandroid.TasksViewModel
import com.example.goneappforandroid.compose.bottomappbar.CustomBottomAppBarItems
import com.example.goneappforandroid.compose.settings.SettingsScreen
import com.example.goneappforandroid.compose.tasks.TasksScreen
import com.example.goneappforandroid.compose.tasks.tutorial.TutorialScreen
import com.example.goneappforandroid.data.Task
import com.example.goneappforandroid.R
import com.example.goneappforandroid.compose.settings.HistoryScreen
import com.example.goneappforandroid.compose.settings.OverViewScreen
import com.example.goneappforandroid.setWeekAlarm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NavGraph(
    navHostController: NavHostController,
    topBarTitle: MutableState<String>,
    tasksList: State<List<Task>>,
    tasksViewModel: TasksViewModel,
    confettiGo: MutableState<Boolean>,
    lazyState: LazyListState,
    alarmManager: AlarmManager,
){
    val local = LocalContext.current
    val cal = remember { mutableStateOf(Calendar.getInstance()) }

    val coroutine = rememberCoroutineScope()


    coroutine.launch {
        while (true) {
            delay(5000)
            cal.value = Calendar.getInstance()
        }
    }
    NavHost(navController = navHostController, startDestination = CustomBottomAppBarItems.Tasks.route) {
        composable(route = CustomBottomAppBarItems.Tasks.route){
            val isFirstStart = prefFirstStart(local, isLoad = true)
            if(isFirstStart){
                topBarTitle.value = stringResource(id = R.string.app_name)
                TutorialScreen(tasksViewModel = tasksViewModel, confettiGo = confettiGo, navHostController = navHostController, local = local)
            } else {
//                setWeekAlarm(alarmManager, local, tasksList.value)
                topBarTitle.value = stringResource(id = R.string.title_today)
                TasksScreen(tasksList = tasksList, tasksViewModel = tasksViewModel, confettiGo = confettiGo, lazyState = lazyState, cal = cal)
            }

        }
        composable(route = CustomBottomAppBarItems.Settings.route){
            topBarTitle.value = stringResource(id = R.string.title_settings)
            CustomAnimation {
                SettingsScreen(navHostController = navHostController)
            }

        }
        composable(route = "overview"){
            topBarTitle.value = stringResource(id = R.string.title_overview)

            CustomAnimation {
                OverViewScreen(tasksList = tasksList, cal = cal, navHostController = navHostController)
            }


        }
        composable(route = "history"){
            topBarTitle.value = stringResource(id = R.string.title_history)
            CustomAnimation {
                HistoryScreen(tasksList = tasksList, cal = cal)
            }

        }
    }
}

@Composable
fun CustomAnimation(content: @Composable() AnimatedVisibilityScope.() -> Unit){
    val currentState = remember { MutableTransitionState(false) }
    currentState.targetState = true
    AnimatedVisibility(visibleState = currentState, enter = fadeIn(tween(800))) {
            content()
    }
}

fun prefFirstStart(local: Context, isLoad: Boolean = false): Boolean{
    val sPref = PreferenceManager.getDefaultSharedPreferences(local)
    return if(isLoad){
        sPref.getBoolean("isFirstStart", true)
    } else {
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putBoolean("isFirstStart", false)
        ed.apply()
        false
    }

}