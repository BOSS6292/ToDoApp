package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// Serializable and Parcelable

@Entity(tableName = ToDoDatabase.TABLE_NAME)
class ToDo: Serializable {

    @PrimaryKey(autoGenerate = true)
    var todoID: Long? = null

    var name: String? = null
}

// Primary key -> Unique identifier -> Auto-incremented
// Name -> Title of the TO-DO (Task)