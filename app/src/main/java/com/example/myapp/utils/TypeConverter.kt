package com.example.myapp.utils

import androidx.room.TypeConverter
import com.example.myapp.model.Ingredient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = json.decodeFromString(value)

    @TypeConverter
    fun fromIngredientList(value: List<Ingredient>): String = json.encodeToString(value)

    @TypeConverter
    fun toIngredientList(value: String): List<Ingredient> = json.decodeFromString(value)
}