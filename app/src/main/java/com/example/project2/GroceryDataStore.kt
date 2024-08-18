package com.example.project2

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class GroceryDataStore(context: Context) {

    private val dataStore: DataStore<List<Grocery>> = DataStoreFactory.create(
        serializer = GrocerySerializer,
        produceFile = { context.dataStoreFile("groceries.json") }
    )

    val groceryFlow: Flow<List<Grocery>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyList())
            } else {
                throw exception
            }
        }

    suspend fun saveGroceries(groceries: List<Grocery>) {
        dataStore.updateData { groceries }
    }
}