package com.example.myapp.ui.recipes.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapp.R
import com.example.myapp.RecipeApplication
import com.example.myapp.databinding.FragmentFavoritesBinding
import com.example.myapp.model.Recipe
import com.example.myapp.ui.recipes.recipeList.RecipesListAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    private lateinit var favoritesViewModel: FavoritesViewModel
    private val adapter: RecipesListAdapter by lazy { RecipesListAdapter(emptyList()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (requireActivity().application as RecipeApplication).appContainer
        favoritesViewModel = appContainer.favoritesViewModelFactory.create()
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

        binding.rvFavorites.adapter = adapter

        favoritesViewModel.favoriteRecipes.observe(viewLifecycleOwner) { recipes ->
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
        favoritesViewModel.refreshFavorites()
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
                findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToRecipeFragment(
                        recipe
                    )
                )
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
