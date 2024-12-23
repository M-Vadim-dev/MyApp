package com.example.myapp.ui.recipes.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.RecipesRepository
import com.example.myapp.model.Recipe
import com.example.myapp.utils.ErrorType
import kotlinx.coroutines.launch

data class RecipeState(
    val recipe: Recipe? = null,
    val isFavorite: Boolean = false,
    val portionsCount: Int = 1,
    val errorType: ErrorType? = null,
)

class RecipeViewModel(private val recipesRepository: RecipesRepository) : ViewModel() {
    private val _state = MutableLiveData(RecipeState())
    val state: LiveData<RecipeState> get() = _state

    internal fun setRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val isFavorite = recipesRepository.isRecipeFavorite(recipe.id)
            _state.value = RecipeState(recipe = recipe, isFavorite = isFavorite)
        }
    }

    internal fun updatePortionCount(portionsCount: Int) {
        _state.postValue(_state.value?.copy(portionsCount = portionsCount))
    }

    internal fun onFavoritesClicked(recipe: Recipe) {
        viewModelScope.launch {
            if (_state.value?.isFavorite == true) recipesRepository.removeRecipeFromFavorites(recipe.id)
            else recipesRepository.addRecipeToFavorites(recipe.id)

            val updatedIsFavorite = !(_state.value?.isFavorite ?: false)
            _state.value = _state.value?.copy(isFavorite = updatedIsFavorite)
        }
    }
}