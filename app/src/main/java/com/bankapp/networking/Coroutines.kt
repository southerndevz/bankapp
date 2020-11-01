package com.bankapp.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T : Any> networkOperation(operation: suspend () -> T) = withContext(Dispatchers.IO) {
    runCatching {
        val data = operation.invoke()
        withContext(Dispatchers.Main) {
            when (data) {
                is Throwable -> ResultState.Error(data)
                else -> ResultState.Success(data)
            }

        }
    }.getOrElse {
        withContext(Dispatchers.Main) {
            ResultState.Error(it)
        }
    }
}

sealed class ResultState<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultState<T>()
    data class Error(val exception: Throwable)
}