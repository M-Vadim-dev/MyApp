package com.example.myapp.ui.recipes.recipeList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.data.ImageLoaderService
import com.example.myapp.databinding.ItemRecipeBinding
import com.example.myapp.model.Recipe
import com.example.myapp.utils.Constants

class RecipesListAdapter(private var dataSet: List<Recipe>) :
    RecyclerView.Adapter<RecipesListAdapter.RecipeViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(recipe: Recipe)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class RecipeViewHolder(binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.ivImageRecipe
        val titleTextView: TextView = binding.tvRecipeName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding =
            ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: RecipeViewHolder, position: Int) {
        val recipe = dataSet[position]
        viewHolder.titleTextView.text = recipe.title

        ImageLoaderService.loadImage(
            viewHolder.itemView.context,
            Constants.API_IMAGES_URL + recipe.imageUrl,
            viewHolder.imageView
        )

        viewHolder.imageView.contentDescription =
            viewHolder.itemView.context.getString(
                R.string.text_image_recipe_description,
                recipe.title
            )

        viewHolder.itemView.setOnClickListener { itemClickListener?.onItemClick(recipe) }
    }

    override fun getItemCount(): Int = dataSet.size

    internal fun updateDataSet(newDataSet: List<Recipe>) {
        dataSet = newDataSet
        notifyDataSetChanged()
    }

}