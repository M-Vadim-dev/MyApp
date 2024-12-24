package com.example.myapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapp.model.Category
import com.example.myapp.model.Recipe
import com.example.myapp.utils.Converters

@Database(entities = [Category::class, Recipe::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriesDao(): CategoriesDao
    abstract fun recipesDao(): RecipesDao

}