@file:OptIn(DelicateCoroutinesApi::class)

package com.example.goneappforandroid

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.goneappforandroid.compose.tasks.durationReturn
import com.example.goneappforandroid.data.Task
import com.example.goneappforandroid.data.TasksRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import java.time.Duration
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class TasksViewModel(private val repository: TasksRepository) : ViewModel() {

    fun insertTask(text: String, minute: Int, hour: Int, day: Int, checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(Task(0, text, minute, hour, day, checked))
        }
    }

    fun checkTask(id: Int, checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkTask(id, checked)
        }
    }

    fun textTask(id: Int, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.textTask(id, text)
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(id)
        }
    }

    val tasks = repository.getTasks()
}

