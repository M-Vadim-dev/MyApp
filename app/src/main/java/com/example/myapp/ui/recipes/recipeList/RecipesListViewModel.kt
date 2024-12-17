package com.example.myapp.ui.recipes.recipeList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.AppDatabase
import com.example.myapp.data.RecipesRepository
import com.example.myapp.model.Recipe
import com.example.myapp.utils.ErrorType
import kotlinx.coroutines.launch
import okio.IOException

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _errorType = MutableLiveData<ErrorType?>()
    val errorType: LiveData<ErrorType?> get() = _errorType

    private val recipesRepository = RecipesRepository.getInstance(application)

    internal fun loadRecipes(categoryId: Int) {
        viewModelScope.launch {
            val result = runCatching {
                val cachedRecipes = recipesRepository.getRecipesFromCacheByCategoryId(categoryId)
                if (cachedRecipes.isNotEmpty()) return@runCatching cachedRecipes

                val remoteRecipes = recipesRepository.getRecipesByCategoryId(categoryId)
                    ?.onEach { recipe ->
                        recipesRepository.insertRecipeIntoCache(recipe.copy(categoryId = categoryId))
                    } ?: throw Exception("Ошибка получения данных")
                return@runCatching remoteRecipes
            }
            result.onSuccess { recipeList ->
                _recipes.postValue(recipeList)
                _errorType.postValue(null)
            }
            result.onFailure {
                Log.e("RecipesListViewModel", "Ошибка при загрузке рецептов", it)

                val errorType = when (it) {
                    is IOException -> ErrorType.NETWORK_ERROR
                    else -> ErrorType.DATA_ERROR
                }
                _errorType.postValue(errorType)
            }
        }
    }
}