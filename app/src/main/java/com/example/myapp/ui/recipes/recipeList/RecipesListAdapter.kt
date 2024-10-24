package com.example.myapp.ui.recipes.recipeList

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.databinding.ItemRecipeBinding
import com.example.myapp.model.Recipe

class RecipesListAdapter(private val dataSet: List<Recipe>) :
    RecyclerView.Adapter<RecipesListAdapter.RecipeViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(recipeId: Int)
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

        val drawable = try {
            val inputStream = viewHolder.itemView.context.assets.open(recipe.imageUrl)
            Drawable.createFromStream(inputStream, null).also {
                inputStream.close()
            }

        } catch (e: Exception) {
            Log.e("RecipesListAdapter", "Error loading image: ${recipe.imageUrl}")
            null
        }

        viewHolder.imageView.setImageDrawable(drawable)
        viewHolder.imageView.contentDescription =
            viewHolder.itemView.context.getString(
                R.string.text_image_recipe_description,
                recipe.title
            )

        viewHolder.itemView.setOnClickListener { itemClickListener?.onItemClick(recipe.id) }
    }

    override fun getItemCount(): Int = dataSet.size
}