package com.example.myapp.ui.recipes.favorites

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.RecipesRepository
import com.example.myapp.model.Recipe
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class FavoritesViewModel(private val context: Context) : ViewModel() {
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
            val favoritesList = RecipesRepository.INSTANCE.getRecipesByIds(favoriteIds)
            _favoriteRecipes.postValue(favoritesList?.toList())
        }
    }

    private fun getFavorites(): Set<Int> {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favoriteSet: Set<String>? = sharedPrefs.getStringSet(KEY_FAVORITE_RECIPES, null)
        return favoriteSet?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

    companion object {
        const val PREFS_NAME = "app_preferences"
        const val KEY_FAVORITE_RECIPES = "favorite_recipes"
    }

    class FavoritesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                return FavoritesViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}