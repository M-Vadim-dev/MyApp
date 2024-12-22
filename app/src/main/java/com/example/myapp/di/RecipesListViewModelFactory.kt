package com.example.myapp.di

import com.example.myapp.data.RecipesRepository
import com.example.myapp.ui.recipes.recipeList.RecipesListViewModel

class RecipesListViewModelFactory(private val recipesRepository: RecipesRepository) :
    Factory<RecipesListViewModel> {

    override fun create(): RecipesListViewModel = RecipesListViewModel(recipesRepository)
}