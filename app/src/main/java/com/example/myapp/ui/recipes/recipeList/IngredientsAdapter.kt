package com.example.myapp.ui.recipes.recipeList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.databinding.ItemIngredientBinding
import com.example.myapp.model.Ingredient

class IngredientsAdapter(private var dataSet: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    var ingredients: List<Ingredient>
        get() = dataSet
        set(value) {
            dataSet = value
            notifyDataSetChanged()
        }

    private var quantity: Int = 1

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

        val quantityText = runCatching {
            "${ingredient.quantity.toInt() * quantity}"
        }.getOrElse {
            runCatching {
                "%.1f".format(ingredient.quantity.toFloat() * quantity)
            }.getOrElse {
                ingredient.quantity
            }
        }

        with(viewHolder) {
            quantityTextView.text = quantityText
            nameTextView.text = ingredient.description
            unitOfMeasure.text = ingredient.unitOfMeasure
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun updateIngredients(progress: Int) {
        quantity = progress
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dataSet.size
}