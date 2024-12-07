package com.example.myapp.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            if (categories == null) Toast.makeText(
                context,
                R.string.error_retrieving_data,
                Toast.LENGTH_SHORT
            ).show()
            else categoriesAdapter.updateDataSet(categories)

            categoriesAdapter.setOnItemClickListener(object :
                CategoriesListAdapter.OnItemClickListener {
                override fun onItemClick(category: Category) {
                    findNavController().navigate(
                        CategoriesListFragmentDirections.actionCategoriesListFragmentToRecipesListFragment(
                            category
                        )

                    )
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}