package com.example.myapp.ui.recipes.favorites

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.RecipesRepository
import com.example.myapp.model.Recipe
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
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
            val favoriteIds = getFavorites()
            if (favoriteIds.isEmpty()) {
                _favoriteRecipes.postValue(emptyList())
                return@launch
            }
            val favoritesList = RecipesRepository.getInstance(getApplication()).getRecipesByIds(favoriteIds)
            _favoriteRecipes.postValue(favoritesList?.toList())
        }
    }

    private fun getFavorites(): Set<Int> {
        val sharedPrefs =
            getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favoriteSet: Set<String>? = sharedPrefs.getStringSet(KEY_FAVORITE_RECIPES, null)
        return favoriteSet?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

    companion object {
        const val PREFS_NAME = "app_preferences"
        const val KEY_FAVORITE_RECIPES = "favorite_recipes"
    }
}