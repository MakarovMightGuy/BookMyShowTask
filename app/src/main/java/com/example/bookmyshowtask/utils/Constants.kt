package com.example.bookmyshowtask.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class Constants {

    companion object {

        const val BASE_URL = "https://api.themoviedb.org/"
        const val IMAGE_URL_PREFIX = "https://image.tmdb.org/t/p/original"
        const val MOVIE_DETAILS_URL = "3/movie/453395"
        const val CREDITS_URL = "3/movie/453395/credits"
        const val API_KEY = "0799fb14a63d4ffe0ff4496b6bd30cfe"
        const val ENGLISH = "en-US"
        const val NO_INTERNET = "No Internet Connection"
        const val CONFIG_ERROR = "Please configure network details"

        fun hasInternetConnection(application: Application): Boolean {
            val connectivityManager = application.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.activeNetworkInfo?.run {
                    return when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }

            return false
        }
    }
}