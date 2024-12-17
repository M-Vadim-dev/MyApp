package com.example.myapp.utils

import com.example.myapp.R

enum class ErrorType(val messageId: Int) {
    NETWORK_ERROR(R.string.error_network),
    DATA_ERROR(R.string.error_retrieving_data),
}