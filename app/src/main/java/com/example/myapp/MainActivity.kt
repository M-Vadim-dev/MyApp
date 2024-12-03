package com.example.myapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import com.example.myapp.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val API_CATEGORY = "https://recipes.androidsprint.ru/api/category"

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.i("!!!", "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        loadCategoriesFromApi()

        binding.btnFavourites.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favorites)
        }

        binding.btnCategory.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_category)
        }

    }

    private fun loadCategoriesFromApi() {
        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                val connection = URL(API_CATEGORY).openConnection() as HttpURLConnection
                connection.connect()

                Log.i("!!!", "Выполняю запрос на потоке: ${Thread.currentThread().name}")
                Log.i("!!!", "responseCode: ${connection.responseCode}")
                Log.i("!!!", "responseMessage: ${connection.responseMessage}")

                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    reader.readText()
                }
            }
            val categories: List<Category> = Json.decodeFromString(response)
            Log.i("!!!", "categories: $categories")
        }
    }
}