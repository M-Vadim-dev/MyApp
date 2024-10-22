package com.example.myapp.ui.recipes.recipe

import androidx.lifecycle.ViewModel
import com.example.myapp.model.Recipe

data class RecipeState(
    val recipe: Recipe? = null,
    val isFavorite: Boolean = false,
    val ingredients: List<String> = emptyList(),
    val method: List<String> = emptyList(),
)

class RecipeViewModel : ViewModel() {
}
