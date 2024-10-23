package com.example.myapp.ui.recipes.recipe

import android.content.Context
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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.R
import com.example.myapp.ui.recipes.recipeList.RecipesListFragment.Companion.ARG_RECIPE
import com.example.myapp.databinding.FragmentRecipeBinding
import com.example.myapp.model.Recipe
import com.example.myapp.ui.recipes.recipeList.IngredientsAdapter
import com.example.myapp.ui.recipes.recipeList.MethodAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")
    private var isFavorite: Boolean = false

    private val viewModel: RecipeViewModel by viewModels()

    private val sharedPrefs by lazy {
        activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

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

        viewModel.state.observe(viewLifecycleOwner) { recipeState ->
            Log.i("!!!", "isFavorite: ${recipeState.isFavorite}")
        }

        val recipe: Recipe? = getRecipeFromArguments()
        recipe?.let {
            initUI(it)
            initRecycler(it)
            setFavorites(it)
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

    private fun initUI(recipe: Recipe) {
        binding.tvLabelRecipe.text = recipe.title
        loadImageFromAssets(recipe.imageUrl)
        binding.btnHeartFavourites.setImageResource(R.drawable.ic_heart_empty)
    }

    private fun updateFavouriteButton(title: String) {
        if (isFavorite) {
            binding.btnHeartFavourites.setImageResource(R.drawable.ic_heart)
            binding.btnHeartFavourites.contentDescription =
                getString(R.string.add_to_favourites, title)
        } else {
            binding.btnHeartFavourites.setImageResource(R.drawable.ic_heart_empty)
            binding.btnHeartFavourites.contentDescription =
                getString(R.string.remove_from_favourites, title)
        }
    }

    private fun initRecycler(recipe: Recipe) {
        binding.rvIngredients.adapter = IngredientsAdapter(recipe.ingredients)
        binding.rvMethod.adapter = MethodAdapter(recipe.method)
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

    private fun loadImageFromAssets(imageFileName: String?) {
        if (imageFileName != null) {
            val drawable = try {
                requireContext().assets.open(imageFileName).use { stream ->
                    Drawable.createFromStream(stream, null)
                }
            } catch (e: Exception) {
                Log.e("RecipeFragment", "Error loading image: $imageFileName", e)
                null
            }
            binding.ivHeaderRecipe.setImageDrawable(drawable)
        } else {
            Log.e("RecipeFragment", "Image file name is null")
            binding.ivHeaderRecipe.setImageDrawable(null)
        }
    }

    private fun saveFavorites(favoriteRecipeId: Set<String>) {
        sharedPrefs?.edit()
            ?.putStringSet(KEY_FAVORITE_RECIPES, favoriteRecipeId)
            ?.apply()
    }

    private fun getFavorites(): MutableSet<String> {
        val favoriteSet: Set<String>? = sharedPrefs?.getStringSet(KEY_FAVORITE_RECIPES, null)
        return HashSet(favoriteSet ?: emptySet())
    }

    private fun setFavorites(recipe: Recipe) {
        val favoriteSet = getFavorites()
        isFavorite = favoriteSet.contains(recipe.id.toString())
        updateFavouriteButton(recipe.title)

        binding.btnHeartFavourites.setOnClickListener {
            addRemoveFavorites(recipe.id.toString(), recipe.title)
        }
    }

    private fun addRemoveFavorites(recipeId: String, title: String) {
        val favoriteSet = getFavorites()
        isFavorite = favoriteSet.contains(recipeId)

        when {
            isFavorite -> favoriteSet.remove(recipeId)
            else -> favoriteSet.add(recipeId)
        }

        saveFavorites(favoriteSet)
        isFavorite = !isFavorite
        updateFavouriteButton(title)
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