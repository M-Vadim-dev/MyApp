package com.example.myapp.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.RecipesRepository
import com.example.myapp.model.Category
import com.example.myapp.utils.ErrorType
import kotlinx.coroutines.launch
import java.io.IOException

data class CategoriesListState(
    val dataSet: List<Category>? = emptyList(),
    val errorMessage: ErrorType? = null,
)

class CategoriesListViewModel(private val recipesRepository: RecipesRepository) : ViewModel() {
    private val _categories = MutableLiveData(CategoriesListState())
    val categories: LiveData<CategoriesListState> get() = _categories

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
                _categories.value = CategoriesListState(dataSet = categories, errorMessage = null)
            }.onFailure { error ->
                Log.e("CategoriesListViewModel", "Ошибка получения данных", error)

                val errorType = when (error) {
                    is IOException -> ErrorType.NETWORK_ERROR
                    else -> ErrorType.DATA_ERROR
                }
                _categories.value = CategoriesListState(errorMessage = errorType)
            }
        }
    }
}