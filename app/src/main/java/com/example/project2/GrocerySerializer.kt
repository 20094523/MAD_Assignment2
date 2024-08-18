package com.example.project2

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.OutputStream

object GrocerySerializer : Serializer<List<Grocery>> {
    private val gson = Gson()

    override val defaultValue: List<Grocery> = emptyList()

    override suspend fun readFrom(input: InputStream): List<Grocery> {
        return try {
            val jsonString = input.bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, object : TypeToken<List<Grocery>>() {}.type)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read Grocery data.", e)
        }
    }

    override suspend fun writeTo(t: List<Grocery>, output: OutputStream) {
        val jsonString = gson.toJson(t)
        output.bufferedWriter().use { it.write(jsonString) }
    }
}