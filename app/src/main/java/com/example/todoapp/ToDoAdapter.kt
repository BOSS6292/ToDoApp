package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToDoAdapter(var toDoList: MutableList<ToDo>, val toDoInterface: ToDoInterface): RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    class ToDoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var nameText: TextView = itemView.findViewById(R.id.title)
        var editButton: ImageView = itemView.findViewById(R.id.edit_icon)
        var deleteButton: ImageView = itemView.findViewById(R.id.delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.to_do_item, parent, false)
        return ToDoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val todo = toDoList[position]
        holder.nameText.text = todo.name

        holder.editButton.setOnClickListener {
            toDoInterface.updateTodoText(toDoList[position])
        }

        holder.deleteButton.setOnClickListener {
            toDoInterface.onDeleteTodo(todo, position)
        }
    }

    override fun getItemCount(): Int {
        return toDoList.size
    }

    fun setList(list: MutableList<ToDo>) {
        toDoList = list
        notifyDataSetChanged()
    }

    fun deleteItem(todo: ToDo, position: Int) {
        toDoList.remove(todo)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }
}