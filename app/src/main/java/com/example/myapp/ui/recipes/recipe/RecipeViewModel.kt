package com.example.myapp.ui.recipes.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapp.model.Recipe

data class RecipeState(
    val recipe: Recipe? = null,
    val isFavorite: Boolean = false,
    val servings: Int = 1,
)

class RecipeViewModel : ViewModel() {
    private val _state = MutableLiveData<RecipeState>()
    val state: LiveData<RecipeState> get() = _state

    init {
        Log.i("!!!", "Initializing RecipeViewModel")
        _state.value = RecipeState(isFavorite = true)
    }
}