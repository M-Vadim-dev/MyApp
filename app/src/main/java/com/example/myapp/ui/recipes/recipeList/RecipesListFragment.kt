package com.example.myapp.ui.recipes.recipeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapp.R
import com.example.myapp.RecipeApplication
import com.example.myapp.data.ImageLoaderService
import com.example.myapp.databinding.FragmentRecipesListBinding
import com.example.myapp.model.Recipe
import com.example.myapp.utils.Constants

class RecipesListFragment : Fragment() {

    private var _binding: FragmentRecipesListBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentRecipesListBinding must not be null")

    private lateinit var recipesListViewModel: RecipesListViewModel
    private val adapter: RecipesListAdapter by lazy { RecipesListAdapter(emptyList()) }
    private val args: RecipesListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (requireActivity().application as RecipeApplication).appContainer
        recipesListViewModel = appContainer.recipesListViewModelFactory.create()
    }

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

        binding.tvLabelRecipes.text = args.category.title

        ImageLoaderService.loadImage(
            requireContext(),
            Constants.API_IMAGES_URL + args.category.imageUrl,
            binding.ivHeaderRecipes
        )

        binding.ivHeaderRecipes.contentDescription =
            getString(R.string.text_image_recipe_description, args.category.title)
        recipesListViewModel.loadRecipes(args.category.id)

        binding.rvRecipes.adapter = adapter

        recipesListViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateDataSet(recipes)

            adapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
                override fun onItemClick(recipe: Recipe) {
                    findNavController().navigate(
                        RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment(
                            recipe
                        )
                    )
                }
            })
        }

        recipesListViewModel.errorType.observe(viewLifecycleOwner) { errorType ->
            errorType?.let { type ->
                Toast.makeText(requireContext(), getString(type.messageId), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}