package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnFavourites.setOnClickListener() {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favorites)
        }

        binding.btnCategory.setOnClickListener() {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_category)
        }
    }
}