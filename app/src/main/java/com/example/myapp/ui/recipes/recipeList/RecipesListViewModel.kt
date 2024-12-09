package com.example.myapp.ui.recipes.recipeList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapp.data.RecipesRepository
import com.example.myapp.utils.ThreadPoolProvider
import com.example.myapp.model.Recipe

class RecipesListViewModel : ViewModel() {
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    fun loadRecipes(categoryId: Int) {
        ThreadPoolProvider.threadPool.execute {
            runCatching {
                RecipesRepository.INSTANCE.getRecipesByCategoryId(categoryId)
            }.onSuccess { recipeList ->
                _recipes.postValue(recipeList ?: emptyList())
            }.onFailure {
                Log.e("RecipesListViewModel", "Ошибка при загрузке рецептов")
            }
        }
    }
}