package com.communalka.app.data.model

import java.io.IOException


sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()

    data class Error(val exception: Throwable) : Result<Nothing>() {

        val isNetworkError: Boolean get() = exception is IOException

    }

    data class ErrorResponse<T>(val data: T): Result<T>()

    object Empty : Result<Nothing>()

    object Loading : Result<Nothing>()

    companion object {

        fun <T> success(data: T) = Success(data)

        fun error(exception: Throwable) = Error(exception)

        fun <T> errorResponse(data: T) = ErrorResponse(data)

        fun empty() = Empty

        fun loading() = Loading

        fun <T> successOrEmpty(list: List<T>): Result<List<T>> {
            return if (list.isEmpty()) Empty else Success(list)
        }
    }




}
