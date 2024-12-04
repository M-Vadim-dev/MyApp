package com.example.myapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import com.example.myapp.model.Category
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val API_CATEGORY = "https://recipes.androidsprint.ru/api/category"
private const val THREADS = 10

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val threadPool: ExecutorService = Executors.newFixedThreadPool(THREADS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        threadPool.execute {
            try {
                val connection = URL(API_CATEGORY).openConnection() as HttpURLConnection
                connection.connect()

                Log.i("!!!", "Выполняю запрос на потоке: ${Thread.currentThread().name}")
                Log.i("!!!", "responseCode: ${connection.responseCode}")
                Log.i("!!!", "responseMessage: ${connection.responseMessage}")

                val response =
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        reader.readText()
                    }

                val categories: List<Category> = Json.decodeFromString(response)

                val categoryIds = categories.map { it.id }
                categoryIds.forEach { categoryId ->
                    threadPool.execute {
                        Log.i("!!!", "Выполняю запрос на потоке: ${Thread.currentThread().name}")
                        loadRecipesForCategory(categoryId)
                    }
                }
            } catch (e: Exception) {
                Log.e("!!!", "Ошибка при выполнении запроса: ${e.message}")
            }
        }

        binding.btnFavourites.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favorites)
        }

        binding.btnCategory.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_category)
        }

    }

    private fun loadRecipesForCategory(categoryId: Int) {
        try {
            val connection =
                URL("$API_CATEGORY/$categoryId/recipes").openConnection() as HttpURLConnection
            connection.connect()

            val response =
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    reader.readText()
                }

            Log.i("!!!", "Рецепты для категории ID $categoryId: $response")

        } catch (e: Exception) {
            Log.e("!!!", "Ошибка при получении рецептов для категории ID $categoryId: ${e.message}")
        }
    }
}