package com.example.myapp.ui.recipes.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapp.R
import com.example.myapp.databinding.FragmentFavoritesBinding
import com.example.myapp.model.Recipe
import com.example.myapp.ui.recipes.recipeList.RecipesListAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    private val viewModel: FavoritesViewModel by viewModels()
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
            when {
                recipes == null -> Toast.makeText(
                    context,
                    R.string.error_retrieving_data,
                    Toast.LENGTH_LONG
                )
                    .show()

                recipes.isEmpty() -> showEmptyState(true)
                else -> initRecycler(recipes)
            }
        }
        viewModel.refreshFavorites()
    }

    private fun showEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvFavorites.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.tvFavorites.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
        }
    }

    private fun initRecycler(recipes: List<Recipe>) {
        adapter.updateDataSet(recipes)

        adapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
            override fun onItemClick(recipe: Recipe) {
                navigateToRecipe(recipe.id)
            }
        })
    }

    private fun navigateToRecipe(recipeId: Int) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToRecipeFragment(recipeId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
