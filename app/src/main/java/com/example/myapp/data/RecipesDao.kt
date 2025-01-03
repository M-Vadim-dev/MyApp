package com.example.myapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapp.model.Recipe

@Dao
interface RecipesDao {
    @Query("SELECT * FROM recipes WHERE category_id = :categoryId")
    fun getRecipesByCategoryId(categoryId: Int): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Int): Recipe?

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipes(recipes: List<Recipe>)

    @Query("SELECT * FROM recipes WHERE is_favorite = 1")
    fun getFavoriteRecipes(): List<Recipe>

    @Query("UPDATE recipes SET is_favorite = 1 WHERE id = :recipeId")
    fun addRecipeToFavorites(recipeId: Int)

    @Query("UPDATE recipes SET is_favorite = 0 WHERE id = :recipeId")
    fun removeRecipeFromFavorites(recipeId: Int)
}