package com.example.myapp.ui.recipes.recipe

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapp.data.STUB
import com.example.myapp.model.Recipe

data class RecipeState(
    val recipe: Recipe? = null,
    val isFavorite: Boolean = false,
    val portionsCount: Int = 1,
    val recipeImage: Drawable? = null,
)

class RecipeViewModel(private val application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData(RecipeState())
    val state: LiveData<RecipeState> get() = _state

    private val sharedPrefs by lazy {
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    init {
        _state.value = RecipeState().copy(isFavorite = true)
    }

    internal fun loadRecipe(recipeId: Int) {
        // TODO: load from network
        val recipe = STUB.getRecipeById(recipeId)
        val isFavorite = getFavorites().contains(recipeId.toString())
        val recipeImage = loadImageFromAssets(recipeId)
        _state.value =
            _state.value?.copy(recipe = recipe, isFavorite = isFavorite, recipeImage = recipeImage)
    }

    internal fun updatePortionCount(portionsCount: Int) {
        _state.value = _state.value?.copy(portionsCount = portionsCount)
    }

    private fun getFavorites(): MutableSet<String> {
        val favoriteSet: Set<String>? =
            sharedPrefs.getStringSet(KEY_FAVORITE_RECIPES, null)
        return HashSet(favoriteSet ?: emptySet())
    }

    internal fun onFavoritesClicked(recipeId: String) {
        _state.value?.let { state ->
            val favoriteSet: MutableSet<String> = getFavorites()

            if (favoriteSet.contains(recipeId)) favoriteSet.remove(recipeId)
            else favoriteSet.add(recipeId)

            saveFavorites(favoriteSet)
            _state.value = state.copy(isFavorite = favoriteSet.contains(recipeId))
        }
    }

    private fun saveFavorites(favoriteRecipeId: Set<String>) {
        sharedPrefs.edit().putStringSet(KEY_FAVORITE_RECIPES, favoriteRecipeId)
            .apply()
    }

    private fun loadImageFromAssets(recipeId: Int): Drawable? {
        return try {
            application.assets.open(PATH_TEMPLATE.format(recipeId)).use { stream ->
                Drawable.createFromStream(stream, null)
            }
        } catch (e: Exception) {
            Log.e("RecipeViewModel", "Error loading image for recipe ID: $recipeId", e)
            null
        }
    }

    private companion object {
        const val PATH_TEMPLATE = "recipes/%d.png"
        const val PREFS_NAME = "app_preferences"
        const val KEY_FAVORITE_RECIPES = "favorite_recipes"
    }
}
