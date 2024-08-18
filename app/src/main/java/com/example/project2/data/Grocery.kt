package com.example.project2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//declare data type. This is the bedrock of the database
@Entity(tableName = "grocery")
data class Grocery(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Int,
    val imageUri: String?
)