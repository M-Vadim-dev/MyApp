package com.example.myapp.ui.recipes.recipe

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapp.data.RecipesRepository
import com.example.myapp.utils.ThreadPoolProvider
import com.example.myapp.model.Recipe
import com.example.myapp.utils.Constants

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
        ThreadPoolProvider.threadPool.execute {
            val result = runCatching {
                val recipe = RecipesRepository.INSTANCE.getRecipeById(recipeId)
                val isFavorite = getFavorites().contains(recipeId.toString())
                val recipeImage = recipe?.let { loadImageFromAssets(recipe.imageUrl) }
                _state.postValue(
                    _state.value?.copy(
                        recipe = recipe, isFavorite = isFavorite, recipeImage = recipeImage
                    )
                )
            }
            result.onFailure {
                Log.e("RecipeViewModel", "Ошибка при загрузке рецепта")
            }
        }
    }

    internal fun updatePortionCount(portionsCount: Int) {
        _state.postValue(_state.value?.copy(portionsCount = portionsCount))
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

    private fun loadImageFromAssets(imageURL: String): Drawable? {
        return try {
            application.assets.open(Constants.PATH_TEMPLATE.format(imageURL)).use { stream ->
                Drawable.createFromStream(stream, null)
            }
        } catch (e: Exception) {
            Log.e("RecipeViewModel", "Ошибка при загрузке изображения: $imageURL", e)
            null
        }
    }

    private companion object {
        const val PREFS_NAME = "app_preferences"
        const val KEY_FAVORITE_RECIPES = "favorite_recipes"
    }
}
