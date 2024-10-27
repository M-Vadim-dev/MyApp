package com.example.myapp.ui.recipes.recipe

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
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
            initRecycler()
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
        viewModel.state.observe(viewLifecycleOwner) { recipeState ->
            Log.i("!!!", "isFavorite: ${recipeState.isFavorite}")
            binding.tvLabelRecipe.text = recipeState.recipe?.title
            binding.ivHeaderRecipe.setImageDrawable(recipeState.recipeImage)
            binding.btnHeartFavourites.setImageResource(
                if (recipeState.isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
            )
            binding.btnHeartFavourites.contentDescription =
                getString(
                    if (recipeState.isFavorite) R.string.remove_from_favourites else R.string.add_to_favourites,
                    recipeState.recipe?.title
                )
            binding.btnHeartFavourites.setOnClickListener {
                viewModel.onFavoritesClicked(recipeState.recipe?.id.toString())
            }
            binding.rvIngredients.adapter =
                recipeState.recipe?.let { IngredientsAdapter(it.ingredients) }
            binding.rvMethod.adapter = recipeState.recipe?.let { MethodAdapter(it.method) }
        }
    }

    private fun initRecycler() {
        setupDivider()
        initSeekBar()
    }

    private fun initSeekBar() {
        binding.tvSeekBarServings.text = getString(R.string.text_servings_seekbar, "1")

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSeekBarServings.text =
                    getString(R.string.text_servings_seekbar, progress.toString())
                (binding.rvIngredients.adapter as? IngredientsAdapter)?.updateIngredients(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
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