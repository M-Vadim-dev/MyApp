package com.example.myapp.di

import android.content.Context
import com.example.myapp.data.AppDatabase
import com.example.myapp.data.CategoriesDao
import com.example.myapp.data.RecipeApiService
import com.example.myapp.data.RecipesDao
import com.example.myapp.data.RecipesRepository
import com.example.myapp.utils.Constants.API_BASE_URL
import com.example.myapp.utils.Constants.CONTENT_TYPE
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class AppContainer(context: Context) {

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder().addInterceptor(logging).build()
    }
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val retrofit = Retrofit.Builder().baseUrl(API_BASE_URL).client(client)
        .addConverterFactory(Json.asConverterFactory(CONTENT_TYPE.toMediaType())).build()

    private val apiService = retrofit.create(RecipeApiService::class.java)

    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val categoriesDao: CategoriesDao = database.categoriesDao()
    private val recipesDao: RecipesDao = database.recipesDao()

    private val repository = RecipesRepository(
        recipesDao,
        categoriesDao,
        apiService,
        ioDispatcher,
    )

    val categoriesListViewModelFactory = CategoriesListViewModelFactory(repository)
    val recipesListViewModelFactory = RecipesListViewModelFactory(repository)
    val favoritesViewModelFactory = FavoritesViewModelFactory(repository)
    val recipeViewModelFactory = RecipeViewModelFactory(repository)
}

