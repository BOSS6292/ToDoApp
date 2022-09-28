package com.example.todoapp

interface ToDoInterface {
    fun updateTodoText(todo: ToDo)
    fun onDeleteTodo(todo: ToDo, position: Int)
}