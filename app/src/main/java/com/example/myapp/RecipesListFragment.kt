package com.example.myapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myapp.CategoriesListFragment.Companion.ARG_CATEGORY_ID
import com.example.myapp.CategoriesListFragment.Companion.ARG_CATEGORY_IMAGE_URL
import com.example.myapp.CategoriesListFragment.Companion.ARG_CATEGORY_NAME
import com.example.myapp.databinding.FragmentRecipesListBinding

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

        initRecycler()
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
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RecipeFragment>(R.id.mainContainer)
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}