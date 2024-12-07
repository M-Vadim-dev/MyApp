package com.example.myapp.ui.recipes.recipeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapp.R
import com.example.myapp.data.RecipesRepository
import com.example.myapp.utils.ThreadPoolProvider
import com.example.myapp.model.Recipe

class RecipesListViewModel : ViewModel() {
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val repository = RecipesRepository.INSTANCE_RECIPES_REPOSITORY

    private val _errorMessage = MutableLiveData<String>()

    fun loadRecipes(categoryId: Int) {
        ThreadPoolProvider.getThreadPool().execute {
            try {
                val recipeList = repository.getRecipesByCategoryId(categoryId)
                _recipes.postValue(recipeList?.toList())

                if (recipeList == null) _errorMessage.postValue("Ошибка получения данных")

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.postValue("Ошибка получения данных")
            }
        }
    }
}