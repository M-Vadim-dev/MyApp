package com.example.myapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.databinding.ItemIngredientBinding

class IngredientsAdapter(private val dataSet: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nameTextView: TextView = binding.tvIngredientDescription
        val quantityTextView: TextView = binding.tvIngredientQuantity
        val unitOfMeasure: TextView = binding.tvIngredientUnitOfMeasure
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding =
            ItemIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: IngredientViewHolder, position: Int) {
        val ingredient = dataSet[position]
        viewHolder.quantityTextView.text = ingredient.quantity
        viewHolder.nameTextView.text = ingredient.description
        viewHolder.unitOfMeasure.text = ingredient.unitOfMeasure
    }

    override fun getItemCount(): Int = dataSet.size
}