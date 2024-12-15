package com.example.myapp.ui.categories

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.RecipesRepository
import com.example.myapp.model.Category
import kotlinx.coroutines.launch

data class CategoriesListState(
    val dataSet: List<Category>? = emptyList(),
    val errorMessage: String? = null,
)

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _categories = MutableLiveData(CategoriesListState())
    val categories: LiveData<CategoriesListState> get() = _categories

    private val recipesRepository = RecipesRepository.getInstance(application)

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = runCatching {
                val cachedCategories = recipesRepository.getCategoriesFromCache()
                if (cachedCategories.isNotEmpty()) return@runCatching cachedCategories

                recipesRepository.getAllCategories()
                    ?.onEach { category ->
                        recipesRepository.insertCategoriesFromCache(category)
                    } ?: throw Exception("Ошибка получения данных")
            }
            result.onSuccess { categories ->
                _categories.value = CategoriesListState(dataSet = categories)
            }.onFailure { e ->
                Log.e("CategoriesListViewModel", "Ошибка получения данных", e)
                _categories.value =
                    CategoriesListState(errorMessage = "Ошибка получения данных")
            }
        }
    }
}