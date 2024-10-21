package com.example.myapp.ui.recipes.recipeList

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myapp.ui.categories.CategoriesListFragment.Companion.ARG_CATEGORY_ID
import com.example.myapp.ui.categories.CategoriesListFragment.Companion.ARG_CATEGORY_IMAGE_URL
import com.example.myapp.ui.categories.CategoriesListFragment.Companion.ARG_CATEGORY_NAME
import com.example.myapp.R
import com.example.myapp.data.STUB
import com.example.myapp.databinding.FragmentRecipesListBinding
import com.example.myapp.ui.recipes.recipe.RecipeFragment

class RecipesListFragment : Fragment() {

    private var _binding: FragmentRecipesListBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipesListBinding must not be null")

    private var categoryId: Int? = null
    private var categoryName: String? = null
    private var categoryImageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().let { args ->
            categoryId = args.getInt(ARG_CATEGORY_ID)
            categoryName = args.getString(ARG_CATEGORY_NAME)
            categoryImageUrl = args.getString(ARG_CATEGORY_IMAGE_URL)
        }

        binding.tvLabelRecipes.text = categoryName
        loadImageFromAssets(categoryImageUrl)
        binding.ivHeaderRecipes.contentDescription =
            getString(R.string.text_image_recipe_description, categoryName)
        initRecycler()
    }

    private fun loadImageFromAssets(imageFileName: String?) {
        if (imageFileName != null) {
            val drawable = try {
                requireContext().assets.open(imageFileName).use { stream ->
                    Drawable.createFromStream(stream, null)
                }
            } catch (e: Exception) {
                Log.e("RecipesListFragment", "Error loading image: $imageFileName", e)
                null
            }
            binding.ivHeaderRecipes.setImageDrawable(drawable)
        } else {
            Log.e("RecipesListFragment", "Image file name is null")
            binding.ivHeaderRecipes.setImageDrawable(null)
        }
    }

    private fun initRecycler() {
        val adapter = RecipesListAdapter(STUB.getRecipesByCategoryId(categoryId ?: 0))
        binding.rvRecipes.adapter = adapter

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_RECIPE = "arg_recipe"
    }
}