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
import androidx.compose.runtime.livedata.observeAsState
import com.example.project2.data.Grocery
import com.example.project2.data.GroceryDao
import com.example.project2.data.GroceryDatabase
import com.example.project2.data.GroceryRepository
import com.example.project2.data.GroceryViewModel


//main activity initializes all of the components for the database and
//invokes navController, view-model etc. Initializes the whole app
class MainActivity : ComponentActivity() {

    private val groceryDao: GroceryDao by lazy {
        GroceryDatabase.getDatabase(application).groceryDao()
    }

    private val groceryRepository: GroceryRepository by lazy {
        GroceryRepository(groceryDao)
    }

    private val groceryViewModel: GroceryViewModel by viewModels {
        GroceryViewModelFactory(groceryRepository)
    }

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

//Reused Composable I saw on the marking scheme. Used for AddItemScreen and EditItemScreen
//Consists of a form, image picker and data validation making sure the button
//doesn't show up until all three variables have been filled.

@SuppressLint("UnrememberedMutableState")
@Composable
fun GroceryForm(
    initialName: String,
    initialAmount: String,
    initialImageUri: Uri?,
    onSubmit: (String, String, Uri?) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    modifier: Modifier = Modifier
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

    Column(modifier = modifier.padding(16.dp)) {
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
            enabled = isSubmitEnabled
        ) {
            Text("Submit")
        }
    }
}

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
    val itemList by groceryViewModel.allGroceries.observeAsState(emptyList())

    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            BackgroundColorModifier { modifier ->
                MainScreen(
                    navController = navController,
                    itemList = itemList,
                    groceryViewModel = groceryViewModel,
                    modifier = modifier
                )
            }
        }
        composable("add_item_screen") {
            BackgroundColorModifier { modifier ->
                AddItemScreen(
                    navController = navController,
                    onItemAdded = { name, amount, imageUri ->
                        groceryViewModel.insert(Grocery(name = name, amount = amount.toInt(), imageUri = imageUri))
                        navController.popBackStack()
                    },
                    modifier = modifier
                )
            }
        }
        composable("edit_item_screen/{itemIndex}") { backStackEntry ->
            val itemIndex = backStackEntry.arguments?.getString("itemIndex")?.toInt() ?: 0
            BackgroundColorModifier { modifier ->
                EditItemScreen(
                    navController = navController,
                    itemList = itemList, // Pass itemList here
                    itemIndex = itemIndex,
                    onItemUpdated = { name, amount, imageUri -> // Ensure onItemUpdated is provided
                        groceryViewModel.update(Grocery(name = name, amount = amount.toInt(), imageUri = imageUri))
                        navController.popBackStack()
                    },
                    modifier = modifier
                )
            }
        }
    }
}

//main screen of the app, lists all of the items. uses navController
//on + button to move to different view. columns and rows created to list
// each item in the itemList. buttons beside each item with edit and delete. CRUD.
// invoke GroceryViewModel.
@Composable
fun MainScreen(navController: NavHostController, itemList: List<Grocery>, groceryViewModel: GroceryViewModel, modifier: Modifier) {
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
                        Button(
                            onClick = {
                                navController.navigate("edit_item_screen/$index")
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Edit")
                        }
                        Button(
                            onClick = {
                                groceryViewModel.delete(grocery)
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

// add item, reuses composable, has a form, data validation done before in
// the reusable's declaration.
@Composable
fun AddItemScreen(
    navController: NavHostController,
    onItemAdded: (String, Int, String?) -> Unit,
    modifier: Modifier
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }

    GroceryForm(
        initialName = name,
        initialAmount = amount,
        initialImageUri = imageUri?.let { Uri.parse(it) },
        onSubmit = { name, amount, imageUri ->
            onItemAdded(name, amount.toInt(), imageUri?.toString())
        },
        onImagePicked = { uri -> imageUri = uri?.toString() },
        modifier = modifier
    )
}


//reuses composable. edit item similar to add item
@Composable
fun EditItemScreen(
    navController: NavHostController,
    itemList: List<Grocery>,
    itemIndex: Int,
    onItemUpdated: (String, Int, String?) -> Unit,
    modifier: Modifier
) {
    val grocery = itemList.getOrNull(itemIndex) ?: return

    GroceryForm(
        initialName = grocery.name,
        initialAmount = grocery.amount.toString(),
        initialImageUri = Uri.parse(grocery.imageUri),
        onSubmit = { name, amount, imageUri ->
            onItemUpdated(name, amount.toInt(), imageUri?.toString())
        },
        onImagePicked = {},
        modifier = modifier
    )
}