package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CategoriesListFragment>(R.id.mainContainer)
            }
        }

        binding.btnFavourites.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<FavoritesFragment>(R.id.mainContainer)
            }
        }

        binding.btnCategory.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<CategoriesListFragment>(R.id.mainContainer)
            }
        }

    }
}