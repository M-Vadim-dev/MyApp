package com.example.myapp.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val THREADS = 10

object ThreadPoolProvider {
    val threadPool: ExecutorService = Executors.newFixedThreadPool(THREADS)

}