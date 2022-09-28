package com.example.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateToDoActivity : AppCompatActivity() {
    lateinit var toDoDatabase: ToDoDatabase
    lateinit var editText: EditText

    var isBeingUpdated = false
    var previousTodo: ToDo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_to_do)

        if (intent.hasExtra(MainActivity.PREVIOUS_TODO)) {
            // Previous To-Do is being updated
            isBeingUpdated = true
            previousTodo = intent.extras?.get(MainActivity.PREVIOUS_TODO) as ToDo
        }

        toDoDatabase = Room.databaseBuilder(applicationContext, ToDoDatabase::class.java, ToDoDatabase.DB_NAME).build()

        editText = findViewById(R.id.to_do_edit_text)

        if (isBeingUpdated) {
            editText.setText(previousTodo?.name.toString())
        }

        val saveButton: Button = findViewById(R.id.save_button)

        saveButton.setOnClickListener {
            val enteredText = editText.text.toString()

            if (TextUtils.isEmpty(enteredText)) {
                Toast.makeText(this, "Text should not be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (isBeingUpdated) {
                // Edit already existing To-Do

                previousTodo?.let {
                    it.name = enteredText
                    updateRow(it)
                }

                /*if (previousTodo != null) {
                    previousTodo?.name = enteredText
                    updateRow(previousTodo as ToDo)
                }*/
            } else {
                // New To-Do
                val todo = ToDo()
                todo.name = enteredText
                insertRow(todo)
            }
        }
    }

    private fun insertRow(todo: ToDo) {
        GlobalScope.launch(Dispatchers.IO) {
            // We will be inserting data on background thread
            val id = toDoDatabase.toDoAppDao().insertTodo(todo)
            println("Insertion thread: ${Thread.currentThread().name}")

            launch(Dispatchers.Main) {
                // UI related
                println("For the intent: ${Thread.currentThread().name}")
                todo.todoID = id

                startMainActivity()
            }
        }
    }

    private fun updateRow(todo: ToDo) {
        GlobalScope.launch(Dispatchers.IO) {
            toDoDatabase.toDoAppDao().updateTodo(todo)

            launch(Dispatchers.Main) {
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this@CreateToDoActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}