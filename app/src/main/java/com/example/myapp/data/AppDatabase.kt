package com.example.myapp.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapp.model.Category
import android.content.Context
import androidx.room.TypeConverters
import com.example.myapp.model.Recipe
import com.example.myapp.utils.Constants
import com.example.myapp.utils.Converters

@Database(entities = [Category::class, Recipe::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriesDao(): CategoriesDao
    abstract fun recipesDao(): RecipesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}