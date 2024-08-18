package com.example.project2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

//Crud using livedata and invoking the GroceryRepository file.
class GroceryViewModel(private val repository: GroceryRepository) : ViewModel() {

    val allGroceries: LiveData<List<Grocery>> = repository.getAllGroceries()

    fun insert(grocery: Grocery) {
        viewModelScope.launch {
            repository.insert(grocery)
        }
    }

    fun update(grocery: Grocery) {
        viewModelScope.launch {
            repository.update(grocery)
        }
    }

    fun delete(grocery: Grocery) {
        viewModelScope.launch {
            repository.delete(grocery)
        }
    }
}