package com.example.myapp.ui.recipes.recipe

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.R
import com.example.myapp.databinding.FragmentRecipeBinding
import com.example.myapp.model.Recipe
import com.example.myapp.ui.recipes.recipeList.IngredientsAdapter
import com.example.myapp.ui.recipes.recipeList.MethodAdapter
import com.example.myapp.ui.recipes.recipeList.RecipesListFragment.Companion.ARG_RECIPE
import com.google.android.material.divider.MaterialDividerItemDecoration

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")

    private val viewModel: RecipeViewModel by viewModels()
    private val ingredientsAdapter: IngredientsAdapter by lazy { IngredientsAdapter(emptyList()) }
    private val methodsAdapter: MethodAdapter by lazy { MethodAdapter(emptyList()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe: Recipe? = getRecipeFromArguments()
        recipe?.let {
            initUI()
            viewModel.loadRecipe(recipe.id)
        }
    }

    private fun getRecipeFromArguments(): Recipe? {
        return requireArguments().let { bundle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(ARG_RECIPE, Recipe::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(ARG_RECIPE)
            }
        }
    }

    private fun initUI() {
        binding.rvIngredients.adapter = ingredientsAdapter
        binding.rvMethod.adapter = methodsAdapter

        viewModel.state.observe(viewLifecycleOwner) { recipeState ->
            recipeState.recipe?.let { recipe ->
                binding.tvLabelRecipe.text = recipe.title
                binding.ivHeaderRecipe.setImageDrawable(recipeState.recipeImage)

                ingredientsAdapter.ingredients = recipe.ingredients
                methodsAdapter.methods = recipe.method

                binding.btnHeartFavourites.setImageResource(
                    if (recipeState.isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
                )
                binding.btnHeartFavourites.contentDescription =
                    getString(
                        if (recipeState.isFavorite) R.string.remove_from_favourites else R.string.add_to_favourites,
                        recipeState.recipe.title
                    )
                binding.btnHeartFavourites.setOnClickListener {
                    viewModel.onFavoritesClicked(recipeState.recipe.id.toString())
                }

                binding.tvSeekBarServings.text =
                    getString(R.string.text_servings_seekbar, recipeState.portionsCount.toString())

                (binding.rvIngredients.adapter as? IngredientsAdapter)?.updateIngredients(
                    recipeState.portionsCount
                )
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(PortionSeekBarListener { progress ->
            viewModel.updatePortionCount(progress)
        })

        setupDivider()
    }

    private class PortionSeekBarListener(val onChangeIngredients: (Int) -> Unit) :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onChangeIngredients(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private fun setupDivider() {
        val dividerItemDecoration =
            MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            ).apply {
                setDividerColor(ContextCompat.getColor(requireContext(), R.color.mint_cream))
                isLastItemDecorated = false
                setDividerInsetStartResource(requireContext(), R.dimen.text_space_12)
                setDividerInsetEndResource(requireContext(), R.dimen.text_space_12)
            }
        binding.rvMethod.addItemDecoration(dividerItemDecoration)
        binding.rvIngredients.addItemDecoration(dividerItemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PREFS_NAME = "app_preferences"
        const val KEY_FAVORITE_RECIPES = "favorite_recipes"
    }
}