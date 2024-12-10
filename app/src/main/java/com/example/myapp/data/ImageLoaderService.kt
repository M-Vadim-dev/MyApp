package com.example.myapp.data

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myapp.R

object ImageLoaderService {

    fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        placeHolder: Int = R.drawable.img_placeholder,
        errorImage: Int = R.drawable.img_error,
    ) {
        Glide.with(context)
            .load(url)
            .placeholder(placeHolder)
            .error(errorImage)
            .into(imageView)
    }
}