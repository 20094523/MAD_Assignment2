package com.example.project2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project2.data.GroceryRepository
import com.example.project2.data.GroceryViewModel


//more boilerplate code for ROOM. mostly just used some tutorials off of forums
//to apply this to my project as I couldnt do the ROOM database lab.
class GroceryViewModelFactory(private val repository: GroceryRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroceryViewModel::class.java)) {
            return GroceryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}