package com.example.myapp.di

import com.example.myapp.data.RecipesRepository
import com.example.myapp.ui.categories.CategoriesListViewModel

class CategoriesListViewModelFactory(private val recipesRepository: RecipesRepository) :
    Factory<CategoriesListViewModel> {

    override fun create(): CategoriesListViewModel = CategoriesListViewModel(recipesRepository)
}