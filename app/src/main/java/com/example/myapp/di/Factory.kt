package com.example.myapp.di

interface Factory<T> {
    fun create(): T

}