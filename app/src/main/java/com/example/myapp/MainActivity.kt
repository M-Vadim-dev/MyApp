package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myapp.databinding.ActivityMainBinding
import com.example.myapp.ui.recipes.favorites.FavoritesFragment
import com.example.myapp.ui.categories.CategoriesListFragment

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
                replaceFragment<FavoritesFragment>()
            }
        }

        binding.btnCategory.setOnClickListener {
            supportFragmentManager.commit {
                replaceFragment<CategoriesListFragment>()
            }
        }

    }

    private inline fun <reified T : Fragment> replaceFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<T>(R.id.mainContainer)
        }
    }
}
