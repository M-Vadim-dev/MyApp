package com.example.myapp.ui.recipes.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.RecipesRepository
import com.example.myapp.model.Recipe
import kotlinx.coroutines.launch

class FavoritesViewModel(private val recipesRepository: RecipesRepository) : ViewModel() {
    private val _favoriteRecipes = MutableLiveData<List<Recipe>>()
    val favoriteRecipes: LiveData<List<Recipe>> get() = _favoriteRecipes

    init {
        loadFavorites()
    }

    fun refreshFavorites() {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val favoritesList = recipesRepository.getAllFavoriteRecipes()
            _favoriteRecipes.postValue(favoritesList)
        }
    }
}