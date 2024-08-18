package com.example.project2
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class GroceryViewModel : ViewModel() {

    val itemList = mutableStateListOf<Grocery>()
}