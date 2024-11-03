package com.example.myapp.ui.recipes.recipeList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.databinding.ItemMethodBinding

class MethodAdapter(var dataSet: List<String>) :
    RecyclerView.Adapter<MethodAdapter.MethodViewHolder>() {

    var methods: List<String>
        get() = dataSet
        set(value) {
            dataSet = value
            notifyDataSetChanged()
        }

    class MethodViewHolder(binding: ItemMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val methodTextView: TextView = binding.tvMethodDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MethodViewHolder {
        val binding =
            ItemMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MethodViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: MethodViewHolder, position: Int) {
        val methodItemList = "${position + 1}. ${dataSet[position]}"
        viewHolder.methodTextView.text = methodItemList
    }

    override fun getItemCount(): Int = dataSet.size
}