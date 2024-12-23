package com.example.myapp.di

import com.example.myapp.data.RecipesRepository
import com.example.myapp.ui.recipes.recipe.RecipeViewModel

class RecipeViewModelFactory(private val recipesRepository: RecipesRepository) :
    Factory<RecipeViewModel> {

    override fun create(): RecipeViewModel = RecipeViewModel(recipesRepository)
}