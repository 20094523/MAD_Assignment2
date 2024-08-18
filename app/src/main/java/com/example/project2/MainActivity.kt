package com.example.project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project2.ui.theme.MAD_Assignment2Theme

data class Item(val name: String, val amount: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_Assignment2Theme {
                val navController = rememberNavController()
                val itemList = remember { mutableStateListOf<Item>() }  // Correctly typed
                AppNavHost(navController = navController, itemList = itemList)
            }
        }
    }
}

//main controller. NavHost for both views.

@Composable
fun AppNavHost(navController: NavHostController, itemList: MutableList<Item>) {
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(navController = navController, itemList = itemList)
        }
        composable("add_item_screen") {
            AddItemScreen(navController = navController, itemList = itemList)
        }
    }
}
@Composable

//view 1. Items Displayed in a list.

fun MainScreen(navController: NavHostController, itemList: List<Item>) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_item_screen") }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (itemList.isEmpty()) {
                Text("No items available", style = MaterialTheme.typography.bodyLarge)
            } else {
                itemList.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(item.name, modifier = Modifier.weight(1f))
                        Text(item.amount.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable

//View 2. Adding to list

fun AddItemScreen(navController: NavHostController, itemList: MutableList<Item>) {
    //start off empty
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter Grocery") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Enter Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && amount.isNotBlank()) {
                    itemList.add(Item(name, amount.toInt()))
                    navController.popBackStack()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Item")
        }
    }
}