package com.example.myapp.ui.categories

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.databinding.ItemCategoryBinding
import com.example.myapp.model.Category

class CategoriesListAdapter(private val dataSet: List<Category>) :
    RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(categoryId: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class ViewHolder(binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.ivImageCategory
        val titleTextView: TextView = binding.tvCategoryName
        val descriptionTextView: TextView = binding.tvCategoryDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val category = dataSet[position]
        viewHolder.titleTextView.text = category.title
        viewHolder.descriptionTextView.text = category.description

        val drawable = try {
            val inputStream = viewHolder.itemView.context.assets.open(category.imageUrl)
            Drawable.createFromStream(inputStream, null).also {
                inputStream.close()
            }

        } catch (e: Exception) {
            Log.e("CategoriesListAdapter", "Error loading image: ${category.imageUrl}")
            null
        }

        viewHolder.imageView.setImageDrawable(drawable)
        viewHolder.imageView.contentDescription =
            viewHolder.itemView.context.getString(
                R.string.text_image_description,
                category.title
            )

        viewHolder.itemView.setOnClickListener { itemClickListener?.onItemClick(category.id) }
    }

    override fun getItemCount(): Int = dataSet.size

}