package com.example.bookmyshowtask.respository

import com.example.bookmyshowtask.api.RetrofitInstance

class TestRepository {

    suspend fun getMovieDetails(
        baseUrl: String
    ) = RetrofitInstance.api(baseUrl).getMovieDetails()

    suspend fun getCredits(
        baseUrl: String
    ) = RetrofitInstance.api(baseUrl).getCredits()
}