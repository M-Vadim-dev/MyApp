package com.example.myapp.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.data.ImageLoaderService
import com.example.myapp.databinding.ItemCategoryBinding
import com.example.myapp.model.Category
import com.example.myapp.utils.Constants

class CategoriesListAdapter(private var dataSet: List<Category>) :
    RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(category: Category)
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

        ImageLoaderService.loadImage(
            viewHolder.itemView.context,
            Constants.API_IMAGES_URL + category.imageUrl,
            viewHolder.imageView
        )

        viewHolder.imageView.contentDescription =
            viewHolder.itemView.context.getString(
                R.string.text_image_description,
                category.title
            )

        viewHolder.itemView.setOnClickListener { itemClickListener?.onItemClick(category) }
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateDataSet(newDataSet: List<Category>) {
        dataSet = newDataSet
        notifyDataSetChanged()
    }
}