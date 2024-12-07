package com.example.myapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import com.example.myapp.model.Category
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val API_CATEGORY = "https://recipes.androidsprint.ru/api/category"

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
//    private val threadPool: ExecutorService = Executors.newFixedThreadPool(THREADS)
//
//    private val client: OkHttpClient by lazy {
//        val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//        OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        threadPool.execute {
//            val request = Request.Builder()
//                .url(API_CATEGORY)
//                .build()
//
//            client.newCall(request).enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    response.use { response ->
//                        Log.i("!!!", "Выполняю запрос на потоке: ${Thread.currentThread().name}")
//                        Log.i("!!!", "responseCode: ${response.code}")
//                        Log.i("!!!", "responseMessage: ${response.message}")
//
//                        val responseBody = response.body?.string() ?: ""
//                        val categories: List<Category> = Json.decodeFromString(responseBody)
//
//                        val categoryIds = categories.map { it.id }
//                        categoryIds.forEach { categoryId ->
//                            threadPool.execute {
//                                Log.i(
//                                    "!!!",
//                                    "Выполняю запрос на потоке: ${Thread.currentThread().name}"
//                                )
//                                loadRecipesForCategory(categoryId)
//                            }
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call, e: IOException) {
//                    Log.e("!!!", "Ошибка при выполнении запроса: ${e.message}")
//                }
//            })
//        }

        binding.btnFavourites.setOnClickListener() {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favorites)
        }

        binding.btnCategory.setOnClickListener() {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_category)
        }

    }

//    private fun loadRecipesForCategory(categoryId: Int) {
//        val request = Request.Builder()
//            .url("$API_CATEGORY/$categoryId/recipes")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                response.body?.string().let { response ->
//                    Log.i("!!!", "Рецепты для категории ID $categoryId: $response")
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e(
//                    "!!!",
//                    "Ошибка при получении рецептов для категории ID $categoryId: ${e.message}"
//                )
//            }
//        })
//
//    }
}