package com.example.myapp.data

import android.util.Log
import com.example.myapp.model.Category
import com.example.myapp.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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

class RecipesRepository {

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

    suspend fun getRecipeById(id: Int): Recipe? = withContext(Dispatchers.IO) {
        safeApiCall(apiService.getRecipeById(id))
    }

    suspend fun getRecipesByIds(ids: Set<Int>): List<Recipe>? = withContext(Dispatchers.IO) {
        val idString = ids.joinToString(",")
        safeApiCall(apiService.getRecipesByIds(idString))
    }

    suspend fun getCategoryById(id: Int): Category? = withContext(Dispatchers.IO) {
        safeApiCall(apiService.getCategoryById(id))
    }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? =
        withContext(Dispatchers.IO) {
            safeApiCall(apiService.getRecipesByCategoryId(categoryId))
        }

    suspend fun getAllCategories(): List<Category>? = withContext(Dispatchers.IO) {
        safeApiCall(apiService.getCategories())
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
        val INSTANCE: RecipesRepository by lazy { RecipesRepository() }
    }
}