package com.example.myapp.ui.recipes.recipeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapp.data.STUB
import com.example.myapp.model.Recipe

class RecipesListViewModel : ViewModel() {
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    fun loadRecipes(categoryId: Int) {
        _recipes.value = STUB.getRecipesByCategoryId(categoryId)
    }
}