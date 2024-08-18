package com.example.project2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.activity.viewModels
import androidx.compose.runtime.Composable

data class Grocery(val name: String, val amount: Int, val imageUri: String?)


class MainActivity : ComponentActivity() {
    private val groceryViewModel: GroceryViewModel by viewModels() // Get ViewModel instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_Assignment2Theme {
                val navController = rememberNavController()
                AppNavHost(navController = navController, groceryViewModel = groceryViewModel)
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun GroceryForm(
    initialName: String,
    initialAmount: String,
    initialImageUri: Uri?,
    onSubmit: (String, String, Uri?) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    modifier: Modifier = Modifier // Default value added
) {
    var name by remember { mutableStateOf(initialName) }
    var amount by remember { mutableStateOf(initialAmount) }
    var imageUri by remember { mutableStateOf(initialImageUri) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        onImagePicked(uri)
    }

    val isSubmitEnabled by derivedStateOf {
        name.isNotBlank() && amount.isNotBlank() && imageUri != null
    }

    Column(modifier = modifier.padding(16.dp)) { // Apply modifier here
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
                if (isSubmitEnabled) {
                    onSubmit(name, amount, imageUri)
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = isSubmitEnabled // Disable button if validation fails
        ) {
            Text("Submit")
        }
    }
}

// Function to get the background color based on orientation
@Composable
fun BackgroundColorModifier(content: @Composable (Modifier) -> Unit) {
    val configuration = LocalConfiguration.current
    val backgroundColor = when (configuration.orientation) {
        android.content.res.Configuration.ORIENTATION_LANDSCAPE -> Color.Cyan
        android.content.res.Configuration.ORIENTATION_PORTRAIT -> Color.Blue
        else -> Color.White
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        content(Modifier.fillMaxSize())
    }
}

@Composable
fun AppNavHost(navController: NavHostController, groceryViewModel: GroceryViewModel) {
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            BackgroundColorModifier { modifier ->
                MainScreen(navController = navController, itemList = groceryViewModel.itemList, modifier = modifier)
            }
        }
        composable("add_item_screen") {
            BackgroundColorModifier { modifier ->
                AddItemScreen(navController = navController, itemList = groceryViewModel.itemList, modifier = modifier)
            }
        }
        composable("edit_item_screen/{itemIndex}") { backStackEntry ->
            val itemIndex = backStackEntry.arguments?.getString("itemIndex")?.toInt() ?: 0
            BackgroundColorModifier { modifier ->
                EditItemScreen(navController = navController, itemList = groceryViewModel.itemList, itemIndex = itemIndex, modifier = modifier)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController, itemList: MutableList<Grocery>, modifier: Modifier) {
    Scaffold(
        modifier = modifier,
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
fun AddItemScreen(navController: NavHostController, itemList: MutableList<Grocery>, modifier: Modifier) {
    GroceryForm(
        initialName = "",
        initialAmount = "",
        initialImageUri = null,
        onSubmit = { name, amount, imageUri ->
            itemList.add(Grocery(name, amount.toInt(), imageUri?.toString()))
            navController.popBackStack()
        },
        onImagePicked = { imageUri -> },
        modifier = modifier
    )
}

@Composable
fun EditItemScreen(navController: NavHostController, itemList: MutableList<Grocery>, itemIndex: Int, modifier: Modifier) {
    val grocery = itemList[itemIndex]

    GroceryForm(
        initialName = grocery.name,
        initialAmount = grocery.amount.toString(),
        initialImageUri = Uri.parse(grocery.imageUri),
        onSubmit = { name, amount, imageUri ->
            itemList[itemIndex] = Grocery(name, amount.toInt(), imageUri?.toString())
            navController.popBackStack()
        },
        onImagePicked = { imageUri -> },
        modifier = modifier
    )
}