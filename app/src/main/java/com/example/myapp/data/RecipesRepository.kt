package com.example.myapp.data

import android.content.Context
import android.util.Log
import com.example.myapp.model.Category
import com.example.myapp.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit

private const val API_BASE_URL = "https://recipes.androidsprint.ru/api/"
private const val CONTENT_TYPE = "application/json"

class RecipesRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    context: Context
) {
    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .client(client)
        .addConverterFactory(Json.asConverterFactory(CONTENT_TYPE.toMediaType()))
        .build()

    private val apiService = retrofit.create(RecipeApiService::class.java)

    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val categoriesDao: CategoriesDao = database.categoriesDao()
    private val recipesDao: RecipesDao = database.recipesDao()

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

    suspend fun getRecipesByIds(ids: Set<Int>): List<Recipe>? = withContext(ioDispatcher) {
        val idString = ids.joinToString(",")
        safeApiCall(apiService.getRecipesByIds(idString))
    }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? =
        withContext(ioDispatcher) {
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
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "RecipesRepository",
                    "Error code: ${response.code()}, message: ${response.message()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("RecipesRepository", "Не удалось подключиться к серверу", e)
            null
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RecipesRepository? = null

        fun getInstance(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO
        ): RecipesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RecipesRepository(ioDispatcher, context).also { INSTANCE = it }
            }
        }
    }
}