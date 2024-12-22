package com.example.myapp.di

import com.example.myapp.data.RecipesRepository
import com.example.myapp.ui.recipes.favorites.FavoritesViewModel

class FavoritesViewModelFactory(private val recipesRepository: RecipesRepository) :
    Factory<FavoritesViewModel> {

    override fun create(): FavoritesViewModel = FavoritesViewModel(recipesRepository)
}