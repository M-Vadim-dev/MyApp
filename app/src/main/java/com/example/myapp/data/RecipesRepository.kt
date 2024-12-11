package com.example.myapp.data

import android.util.Log
import com.example.myapp.model.Category
import com.example.myapp.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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

    fun getRecipeById(id: Int): Recipe? = safeApiCall(apiService.getRecipeById(id))

    fun getRecipesByIds(ids: Set<Int>): List<Recipe>? {
        val idString = ids.joinToString(",")
        return safeApiCall(apiService.getRecipesByIds(idString))
    }

    fun getCategoryById(id: Int): Category? = safeApiCall(apiService.getCategoryById(id))

    fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? =
        safeApiCall(apiService.getRecipesByCategoryId(categoryId))

    fun getAllCategories(): List<Category>? = safeApiCall(apiService.getCategories())

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
                if (response.code() == 500) {
                    Log.e(
                        "RecipesRepository",
                        "Internal server error. Body: ${response.errorBody()?.string()}"
                    )
                }
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