package com.example.myapp.ui.categories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.RecipesRepository
import com.example.myapp.model.Category
import kotlinx.coroutines.launch

class CategoriesListViewModel(application: Application) : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val recipesRepository = RecipesRepository.getInstance(application)

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val cachedCategories = recipesRepository.getCategoriesFromCache()
            if (cachedCategories.isNotEmpty()) {
                _categories.postValue(cachedCategories)
            }

            val remoteCategories = runCatching {
                recipesRepository.getAllCategories()
            }.onFailure { e ->
                Log.e("CategoriesListViewModel", "Ошибка получения данных", e)
            }.getOrNull()

            if (remoteCategories != null) {
                remoteCategories.forEach { category ->
                    recipesRepository.insertCategoriesFromCache(category)
                }
                _categories.postValue(remoteCategories)
            }
        }
    }
}

class CategoriesListViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriesListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}