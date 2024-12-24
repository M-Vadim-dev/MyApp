package com.example.myapp.data

import android.util.Log
import com.example.myapp.di.IoDispatcher
import com.example.myapp.model.Category
import com.example.myapp.model.Recipe
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Call

class RecipesRepository @Inject constructor(
    private val recipesDao: RecipesDao,
    private val categoriesDao: CategoriesDao,
    private val apiService: RecipeApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getCategoriesFromCache(): List<Category> = withContext(ioDispatcher) {
        categoriesDao.getAllCategories()
    }

    suspend fun insertCategoriesFromCache(category: Category) = withContext(ioDispatcher) {
        categoriesDao.insertCategory(category)
    }

    suspend fun getRecipesFromCacheByCategoryId(categoryId: Int): List<Recipe> =
        withContext(ioDispatcher) {
            recipesDao.getRecipesByCategoryId(categoryId)
        }

    suspend fun insertRecipeIntoCache(recipe: Recipe) = withContext(ioDispatcher) {
        recipesDao.insertRecipe(recipe)
    }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? = withContext(ioDispatcher) {
        safeApiCall(apiService.getRecipesByCategoryId(categoryId))
    }

    suspend fun getAllCategories(): List<Category>? = withContext(ioDispatcher) {
        safeApiCall(apiService.getCategories())
    }

    suspend fun addRecipeToFavorites(recipeId: Int) = withContext(ioDispatcher) {
        recipesDao.addRecipeToFavorites(recipeId)
    }

    suspend fun removeRecipeFromFavorites(recipeId: Int) = withContext(ioDispatcher) {
        recipesDao.removeRecipeFromFavorites(recipeId)
    }

    suspend fun getAllFavoriteRecipes(): List<Recipe> = withContext(ioDispatcher) {
        recipesDao.getFavoriteRecipes()
    }

    suspend fun isRecipeFavorite(recipeId: Int): Boolean = withContext(ioDispatcher) {
        recipesDao.getRecipeById(recipeId)?.isFavorite ?: false
    }

    private fun <T> safeApiCall(call: Call<T>): T? {
        return try {
            val response = call.execute()
            if (response.isSuccessful) response.body()
            else null
        } catch (e: Exception) {
            Log.e("RecipesRepository", "Не удалось подключиться к серверу", e)
            null
        }
    }
}