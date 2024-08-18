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
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import coil.compose.AsyncImage
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

data class Grocery(val name: String, val amount: Int, val imageUri: String?)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_Assignment2Theme {
                val navController = rememberNavController()
                val itemList = remember { mutableStateListOf<Grocery>() }  // Correctly typed
                AppNavHost(navController = navController, itemList = itemList)
            }
        }
    }
}

//main controller. NavHost for both views.

@Composable
fun AppNavHost(navController: NavHostController, itemList: MutableList<Grocery>) {
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(navController = navController, itemList = itemList)
        }
        composable("add_item_screen") {
            AddItemScreen(navController = navController, itemList = itemList)
        }
        composable("edit_item_screen/{itemIndex}") { backStackEntry ->
            val itemIndex = backStackEntry.arguments?.getString("itemIndex")?.toInt() ?: 0
            EditItemScreen(navController = navController, itemList = itemList, itemIndex = itemIndex)
        }
    }
}
@Composable

//view 1. Items Displayed in a list.

fun MainScreen(navController: NavHostController, itemList: MutableList<Grocery>) {
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
                itemList.forEachIndexed { index, grocery ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display the image if available
                        if (grocery.imageUri != null) {
                            AsyncImage(
                                model = grocery.imageUri,
                                contentDescription = "Item Image",
                                modifier = Modifier.weight(1f).padding(8.dp)
                            )
                        } else {
                            Image(
                                bitmap = ImageBitmap.imageResource(R.drawable.placeholder),
                                contentDescription = "Placeholder",
                                modifier = Modifier.weight(1f).padding(8.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(2f)) {
                            Text(grocery.name)
                            Text(grocery.amount.toString())
                        }

                        // Edit Button
                        Button(
                            onClick = {
                                navController.navigate("edit_item_screen/$index")
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Edit")
                        }

                        // Delete Button
                        Button(
                            onClick = {
                                itemList.removeAt(index)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable

//View 2. Adding to list

fun AddItemScreen(navController: NavHostController, itemList: MutableList<Grocery>) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter Name") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Enter Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Pick Image")
        }

        imageUri?.let {
            AsyncImage(model = it, contentDescription = null, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && amount.isNotBlank()) {
                    itemList.add(Grocery(name, amount.toInt(), imageUri.toString()))
                    navController.popBackStack()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Item")
        }
    }
}

    @Composable
    fun EditItemScreen(navController: NavHostController, itemList: MutableList<Grocery>, itemIndex: Int) {
        var name by remember { mutableStateOf(itemList[itemIndex].name) }
        var amount by remember { mutableStateOf(itemList[itemIndex].amount.toString()) }
        var imageUri by remember { mutableStateOf<Uri?>(Uri.parse(itemList[itemIndex].imageUri)) }

        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
        }

        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter Name") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Enter Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Pick Image")
            }

            imageUri?.let {
                AsyncImage(model = it, contentDescription = null, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && amount.isNotBlank()) {
                        itemList[itemIndex] = Grocery(name, amount.toInt(), imageUri.toString())
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }