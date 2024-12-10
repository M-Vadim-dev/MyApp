package com.example.myapp.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapp.data.RecipesRepository
import com.example.myapp.utils.ThreadPoolProvider
import com.example.myapp.model.Category

class CategoriesListViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        ThreadPoolProvider.threadPool.execute {
            runCatching {
                RecipesRepository.INSTANCE.getAllCategories()
            }.onSuccess { categories ->
                _categories.postValue(categories ?: emptyList())
            }.onFailure { e ->
                Log.e("CategoriesListViewModel", "Ошибка получения данных", e)
            }
        }
    }
}