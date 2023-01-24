package com.example.bookmyshowtask.api

import com.example.bookmyshowtask.models.credits.CreditsResponse
import com.example.bookmyshowtask.models.moviedetails.MovieDetailsResponse
import com.example.bookmyshowtask.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TestAPI {

    @GET(Constants.MOVIE_DETAILS_URL)
    suspend fun getMovieDetails(
        @Query("api_key")
        apiKey: String = Constants.API_KEY,
        @Query("language")
        language: String = Constants.ENGLISH,
        @Query("page")
        page: Int = 1
    ): Response<MovieDetailsResponse>

    @GET(Constants.CREDITS_URL)
    suspend fun getCredits(
        @Query("api_key")
        apiKey: String = Constants.API_KEY,
        @Query("language")
        language: String = Constants.ENGLISH
    ): Response<CreditsResponse>
}
