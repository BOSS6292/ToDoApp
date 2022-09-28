package com.example.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ToDoInterface {
    lateinit var recyclerView: RecyclerView
    lateinit var toDoAdapter: ToDoAdapter
    lateinit var toDoDatabase: ToDoDatabase

    companion object {
        const val PREVIOUS_TODO = "PreviousTodo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab: FloatingActionButton = findViewById(R.id.add_todo)

        /*
        This is same as below, where it has been done using lambda expression

        fab.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@MainActivity, CreateToDoActivity::class.java)
                startActivity(intent)
            }
        })
        */

        fab.setOnClickListener {
            val intent = Intent(this, CreateToDoActivity::class.java)
            startActivity(intent)
        }

        toDoDatabase = Room.databaseBuilder(applicationContext, ToDoDatabase::class.java, ToDoDatabase.DB_NAME).build()
        val list = mutableListOf<ToDo>()

        recyclerView = findViewById(R.id.recycler_view)
        toDoAdapter = ToDoAdapter(list, this)
        recyclerView.adapter = toDoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchToDoList()
    }

    private fun fetchToDoList() {
        GlobalScope.launch(Dispatchers.IO) {
            val todoList = toDoDatabase.toDoAppDao().fetchList()

            launch(Dispatchers.Main) {
                // UI updation needs to be done on the main thread
                toDoAdapter.setList(todoList)
            }
        }
    }

    override fun updateTodoText(todo: ToDo) {
        val intent = Intent(this, CreateToDoActivity::class.java)
        intent.putExtra(PREVIOUS_TODO, todo)
        startActivity(intent)
    }

    override fun onDeleteTodo(todo: ToDo, position: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            toDoDatabase.toDoAppDao().deleteTodo(todo)

            launch(Dispatchers.Main) {
                toDoAdapter.deleteItem(todo, position)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchTodos(newText)
                return true
            }
        })

        return true
    }

    private fun searchTodos(newText: String?) {
        if (newText == null) return

        GlobalScope.launch(Dispatchers.IO) {
            val list = toDoDatabase.toDoAppDao().fetchList()

            launch(Dispatchers.Main) {
                val filteredList = filter(list, newText)

                toDoAdapter.setList(filteredList)
                recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun filter(list: List<ToDo>, newText: String): MutableList<ToDo> {
        val lowerCaseText = newText.lowercase()
        val filteredList: MutableList<ToDo> = mutableListOf()

        for (item in list) {
            val text = item.name?.lowercase()

            if (text?.contains(lowerCaseText) == true) {
                filteredList.add(item)
            }
        }

        return filteredList
    }
}