package com.example.goneappforandroid.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDAO {
    @Insert
    suspend fun insertTask(task: Task)

    @Query("UPDATE task_table SET checked = :checked WHERE id = :id")
    suspend fun checkTask(id: Int, checked: Boolean)

    @Query("UPDATE task_table SET text = :text WHERE id = :id")
    suspend fun textTask(id: Int, text: String)

    @Query("DELETE FROM task_table WHERE id = :id")
    suspend fun deleteTask(id: Int)


    @Query("SELECT * FROM task_table")
    fun getTasks(): Flow<List<Task>>
}