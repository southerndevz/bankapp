package com.bankapp.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Coroutines operation */

suspend fun <T : Any> networkOperation(operation: suspend () -> T) = withContext(Dispatchers.IO) {
    /** The coroutine starts by using the Dispatchers.IO context for IO operations */
    runCatching {
        val data = operation.invoke()
        withContext(Dispatchers.Main) {
            /** Afterward the coroutine switches to Dispatchers.Main thread to display result if any on the main thread */
            when (data) {
                is Throwable -> ResultState.Error(data)
                else -> ResultState.Success(data)
            }

        }
    }.getOrElse {
        withContext(Dispatchers.Main) {
            /** The coroutine switches to Dispatchers.Main thread to display an error if the network operation failed */
            ResultState.Error(it)
        }
    }
}

/** The classes of this generic sealed class will help encapsulate the operation result that are returned to the viewModel */
sealed class ResultState<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultState<T>()
    data class Error(val exception: Throwable)
}