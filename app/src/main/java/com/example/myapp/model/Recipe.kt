package com.example.myapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "category_id") val categoryId: Int = -1,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "ingredients") val ingredients: List<Ingredient>,
    @ColumnInfo(name = "method") val method: List<String>,
    @ColumnInfo(name = "image_url") val imageUrl: String,
) : Parcelable