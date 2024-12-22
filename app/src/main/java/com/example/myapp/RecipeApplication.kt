package com.example.myapp

import android.app.Application
import com.example.myapp.di.AppContainer

class RecipeApplication: Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
    }
}