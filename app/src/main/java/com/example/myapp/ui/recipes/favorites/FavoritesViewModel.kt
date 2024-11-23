package com.example.myapp.ui.recipes.favorites

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapp.data.STUB
import com.example.myapp.model.Recipe

class FavoritesViewModel(private val context: Context) : ViewModel() {
    private val _favoriteRecipes = MutableLiveData<List<Recipe>>()
    val favoriteRecipes: LiveData<List<Recipe>> get() = _favoriteRecipes

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        val favoriteIds = getFavorites()
        _favoriteRecipes.value = STUB.getRecipesByIds(favoriteIds)
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