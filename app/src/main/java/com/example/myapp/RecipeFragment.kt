package com.example.myapp

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.RecipesListFragment.Companion.ARG_RECIPE
import com.example.myapp.databinding.FragmentRecipeBinding
import com.google.android.material.divider.MaterialDividerItemDecoration

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")
    private var isFavourite: Boolean = false

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
        if (isFavourite) {
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
        val sharedPrefs = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs?.edit()) {
            this?.putStringSet(KEY_FAVORITE_RECIPES, favoriteRecipeId)
            this?.apply()
        }
        Log.d("RecipeFragment", "Favorites updated: $favoriteRecipeId")
    }

    private fun getFavorites(): MutableSet<String> {
        val sharedPrefs = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favoriteSet: Set<String>? = sharedPrefs?.getStringSet(KEY_FAVORITE_RECIPES, null)
        return HashSet(favoriteSet ?: emptySet())
    }

    private fun setFavorites(recipe: Recipe) {
        val favoriteSet = getFavorites()
        isFavourite = favoriteSet.contains(recipe.id.toString())
        updateFavouriteButton(recipe.title)

        binding.btnHeartFavourites.setOnClickListener {
            addRemoveFavorites(recipe.id.toString(), recipe.title)
        }
    }

    private fun addRemoveFavorites(recipeId: String, title: String) {
        val favoriteSet = getFavorites()
        when {
            favoriteSet.contains(recipeId) -> favoriteSet.remove(recipeId)
            else -> favoriteSet.add(recipeId)
        }
        saveFavorites(favoriteSet)
        isFavourite = favoriteSet.contains(recipeId)
        updateFavouriteButton(title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_FAVORITE_RECIPES = "favorite_recipes"
    }
}