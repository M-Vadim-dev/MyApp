package com.example.myapp.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapp.R
import com.example.myapp.databinding.FragmentListCategoriesBinding
import com.example.myapp.model.Category

class CategoriesListFragment : Fragment() {

    private var _binding: FragmentListCategoriesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListCategoriesBinding must not be null")

    private val viewModel: CategoriesListViewModel by viewModels()
    private val categoriesAdapter: CategoriesListAdapter by lazy { CategoriesListAdapter(emptyList()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCategories.adapter = categoriesAdapter

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesAdapter.updateDataSet(categories)

            categoriesAdapter.setOnItemClickListener(object :
                CategoriesListAdapter.OnItemClickListener {
                override fun onItemClick(category: Category) {
                    openRecipesByCategory(category)
                }
            })
        }
    }

    private fun openRecipesByCategory(category: Category) {
        val bundle = bundleOf(
            ARG_CATEGORY_ID to category.id,
            ARG_CATEGORY_NAME to category.title,
            ARG_CATEGORY_IMAGE_URL to category.imageUrl
        )
        findNavController().navigate(
            R.id.action_categoriesListFragment_to_recipesListFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_CATEGORY_ID = "arg_category_id"
        const val ARG_CATEGORY_NAME = "arg_category_name"
        const val ARG_CATEGORY_IMAGE_URL = "arg_category_image_url"
    }
}