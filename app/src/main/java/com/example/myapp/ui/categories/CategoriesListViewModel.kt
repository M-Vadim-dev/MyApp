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

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val recipesRepository = RecipesRepository.getInstance(application)

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val cachedCategories = recipesRepository.getCategoriesFromCache()
            if (cachedCategories.isNotEmpty()) _categories.postValue(cachedCategories)

            val remoteCategories = runCatching {
                recipesRepository.getAllCategories()
            }.onFailure { e ->
                Log.e("CategoriesListViewModel", "Ошибка получения данных", e)
            }.getOrNull()

            remoteCategories?.let {
                it.forEach { category ->
                    recipesRepository.insertCategoriesFromCache(category)
                }
                _categories.postValue(it)
            }
        }
    }
}