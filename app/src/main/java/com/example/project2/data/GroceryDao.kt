package com.example.project2.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

//CRUD functionality for ROOM database.
@Dao
interface GroceryDao {
    @Query("SELECT * FROM grocery")
    fun getAllGroceries(): LiveData<List<Grocery>>

    @Insert
    suspend fun insert(grocery: Grocery)

    @Update
    suspend fun update(grocery: Grocery)

    @Delete
    suspend fun delete(grocery: Grocery)
}