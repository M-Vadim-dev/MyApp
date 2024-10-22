package com.example.myapp.ui.recipes.favorites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myapp.R
import com.example.myapp.data.STUB
import com.example.myapp.ui.recipes.recipe.RecipeFragment.Companion.KEY_FAVORITE_RECIPES
import com.example.myapp.ui.recipes.recipe.RecipeFragment.Companion.PREFS_NAME
import com.example.myapp.ui.recipes.recipeList.RecipesListFragment.Companion.ARG_RECIPE
import com.example.myapp.databinding.FragmentFavoritesBinding
import com.example.myapp.ui.recipes.recipe.RecipeFragment
import com.example.myapp.ui.recipes.recipeList.RecipesListAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    private val sharedPrefs by lazy {
        activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
    }

    private fun initRecycler() {
        val favoriteIds = getFavorites()
        val recipes = STUB.getRecipesByIds(favoriteIds)
        val adapter = RecipesListAdapter(recipes)
        binding.rvFavorites.adapter = adapter

        if (recipes.isEmpty()) {
            binding.tvFavorites.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.tvFavorites.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
        }

        adapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
            override fun onItemClick(recipeId: Int) {
                openRecipeByRecipeId(recipeId)
            }
        })
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val recipe = STUB.getRecipeById(recipeId)
        if (recipe != null) {
            val bundle = bundleOf(ARG_RECIPE to recipe)
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace<RecipeFragment>(R.id.mainContainer, args = bundle)
                addToBackStack(null)
            }
        } else Log.e("RecipesListFragment", "Recipe not found for ID: $recipeId")
    }

    private fun getFavorites(): Set<Int> {
        val favoriteSet: Set<String>? = sharedPrefs?.getStringSet(KEY_FAVORITE_RECIPES, null)
        return favoriteSet?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}