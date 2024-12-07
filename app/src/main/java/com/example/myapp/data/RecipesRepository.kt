package com.example.myapp.data

import android.util.Log
import com.example.myapp.model.Category
import com.example.myapp.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit

private const val API_BASE_URL = "https://recipes.androidsprint.ru/api/"
private const val CONTENT_TYPE = "application/json"

class RecipesRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(Json.asConverterFactory(CONTENT_TYPE.toMediaType()))
        .build()

    private val apiService = retrofit.create(RecipeApiService::class.java)


    private fun <T> safeApiCall(call: Call<T>): T? {
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("RecipesRepository", "Не удалось подключиться к серверу", e)
            null
        }
    }

    fun getRecipeById(id: Int): Recipe? = safeApiCall(apiService.getRecipeById(id))


    fun getRecipesByIds(ids: Set<Int>): List<Recipe>? {
        val idString = ids.joinToString(",")
        return safeApiCall(apiService.getRecipesByIds(idString))
    }

    fun getCategoryById(id: Int): Category? = safeApiCall(apiService.getCategoryById(id))


    fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? =
        safeApiCall(apiService.getRecipesByCategoryId(categoryId))


    fun getAllCategories(): List<Category>? = safeApiCall(apiService.getCategories())


    fun getImageUrlById(recipeId: Int): String {
        val recipe = getRecipeById(recipeId)
        return recipe?.imageUrl.toString()
    }

    companion object {
        val INSTANCE_RECIPES_REPOSITORY: RecipesRepository by lazy { RecipesRepository() }
    }
}