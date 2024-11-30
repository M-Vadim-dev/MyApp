package com.example.myapp.ui.recipes.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapp.R
import com.example.myapp.databinding.FragmentFavoritesBinding
import com.example.myapp.model.Recipe
import com.example.myapp.ui.recipes.recipeList.RecipesListAdapter
import com.example.myapp.ui.recipes.recipeList.RecipesListFragment.Companion.ARG_RECIPE

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModel.FavoritesViewModelFactory(requireContext())
    }
    private val adapter: RecipesListAdapter by lazy { RecipesListAdapter(emptyList()) }

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

        binding.rvFavorites.adapter = adapter

        viewModel.favoriteRecipes.observe(viewLifecycleOwner) { recipes ->
            initRecycler(recipes)
        }
    }

    private fun initRecycler(recipes: List<Recipe>) {
        adapter.updateDataSet(recipes)

        if (recipes.isEmpty()) {
            binding.tvFavorites.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.tvFavorites.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
        }

        adapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
            override fun onItemClick(recipe: Recipe) {
                openRecipeByRecipe(recipe)
            }
        })
    }

    private fun openRecipeByRecipe(recipe: Recipe) {
        val bundle = bundleOf(ARG_RECIPE to recipe)
        findNavController().navigate(R.id.recipeFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
