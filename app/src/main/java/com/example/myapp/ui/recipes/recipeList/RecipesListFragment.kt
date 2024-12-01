package com.example.myapp.ui.recipes.recipeList

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapp.R
import com.example.myapp.databinding.FragmentRecipesListBinding
import com.example.myapp.model.Recipe
import com.example.myapp.ui.categories.CategoriesListFragment.Companion.ARG_CATEGORY_ID
import com.example.myapp.ui.categories.CategoriesListFragment.Companion.ARG_CATEGORY_IMAGE_URL
import com.example.myapp.ui.categories.CategoriesListFragment.Companion.ARG_CATEGORY_NAME

class RecipesListFragment : Fragment() {

    private var _binding: FragmentRecipesListBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipesListBinding must not be null")

    private var categoryId: Int? = null
    private var categoryName: String? = null
    private var categoryImageUrl: String? = null

    private val viewModel: RecipesListViewModel by viewModels()
    private val adapter: RecipesListAdapter by lazy { RecipesListAdapter(emptyList()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesListBinding.inflate(layoutInflater)
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

        viewModel.loadRecipes(categoryId ?: 0)

        binding.rvRecipes.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateDataSet(recipes)

            adapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
                override fun onItemClick(recipe: Recipe) {
                    openRecipeByRecipe(recipe)
                }
            })
        }
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

    private fun openRecipeByRecipe(recipe: Recipe) {
        val bundle = bundleOf(ARG_RECIPE to recipe)
        findNavController().navigate(R.id.action_recipesListFragment_to_recipeFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_RECIPE = "arg_recipe"
    }
}