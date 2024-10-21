package com.example.myapp.ui.recipes.recipeList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.databinding.ItemIngredientBinding
import com.example.myapp.data.Ingredient
import java.math.BigDecimal
import java.math.RoundingMode

class IngredientsAdapter(private val dataSet: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

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

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(viewHolder: IngredientViewHolder, position: Int) {
        val ingredient = dataSet[position]

        val calculatedQuantity = BigDecimal(ingredient.quantity) * BigDecimal(quantity)
        val quantityText = calculatedQuantity
            .setScale(1, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()

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