package com.example.project2.data

import androidx.lifecycle.LiveData


class GroceryRepository(private val groceryDao: GroceryDao) {

    fun getAllGroceries(): LiveData<List<Grocery>> = groceryDao.getAllGroceries()

    suspend fun insert(grocery: Grocery) {
        groceryDao.insert(grocery)
    }

    suspend fun update(grocery: Grocery) {
        groceryDao.update(grocery)
    }

    suspend fun delete(grocery: Grocery) {
        groceryDao.delete(grocery)
    }
}