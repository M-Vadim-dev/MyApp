package com.example.myapp.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val THREADS = 10

object ThreadPoolProvider {
    private val threadPool: ExecutorService = Executors.newFixedThreadPool(THREADS)

    fun getThreadPool(): ExecutorService = threadPool

}