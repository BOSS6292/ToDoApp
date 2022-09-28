package com.example.todoapp

import androidx.room.*

// DAO -> Data Access Objects
// CRUD -> Create, Read, Update, Delete

@Dao
interface ToDoDao {

    @Insert
    suspend fun insertTodo(todo: ToDo): Long

    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME) // SELECT * FROM todo
    suspend fun fetchList(): MutableList<ToDo>

    @Update
    suspend fun updateTodo(todo: ToDo)

    @Delete
    suspend fun deleteTodo(todo: ToDo)

    /*
    @Query("DELETE FROM " + ToDoDatabase.TABLE_NAME)
    suspend fun clear()
    */
}