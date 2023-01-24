package com.example.bookmyshowtask.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookmyshowtask.models.credits.CreditsResponse
import com.example.bookmyshowtask.models.moviedetails.MovieDetailsResponse
import com.example.bookmyshowtask.respository.TestRepository
import com.example.bookmyshowtask.utils.Constants
import com.example.bookmyshowtask.utils.Resource
import kotlinx.coroutines.launch
import okio.IOException
import org.json.JSONObject
import retrofit2.Response

class MovieDetailsViewModel(
    application: Application,
    private val testRepository: TestRepository
) : AndroidViewModel(application) {

    val getMovieDetailsMutableLiveData: MutableLiveData<Resource<MovieDetailsResponse>> =
        MutableLiveData()

    fun getMovieDetails(
        baseUrl: String
    ) = viewModelScope.launch {
        safeAPICallGetMovieDetails(baseUrl)
    }

    private suspend fun safeAPICallGetMovieDetails(
        baseUrl: String
    ) {
        getMovieDetailsMutableLiveData.postValue(Resource.Loading())
        try {
            if (Constants.hasInternetConnection(getApplication())) {
                val response = testRepository.getMovieDetails(baseUrl)
                getMovieDetailsMutableLiveData.postValue(handleMovieDetailsResponse(response))
            } else {
                getMovieDetailsMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getMovieDetailsMutableLiveData.postValue(Resource.Error("${t.message}"))
                else -> getMovieDetailsMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleMovieDetailsResponse(response: Response<MovieDetailsResponse>): Resource<MovieDetailsResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { movieDetailsResponse ->
                return Resource.Success(movieDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString("errorMessage")
            }
        }
        return Resource.Error(errorMessage)
    }

    val getCreditsMutableLiveData: MutableLiveData<Resource<CreditsResponse>> = MutableLiveData()

    fun getCredits(
        baseUrl: String
    ) = viewModelScope.launch {
        safeAPICallGetCredits(baseUrl)
    }

    private suspend fun safeAPICallGetCredits(
        baseUrl: String
    ) {
        getCreditsMutableLiveData.postValue(Resource.Loading())
        try {
            if (Constants.hasInternetConnection(getApplication())) {
                val response = testRepository.getCredits(baseUrl)
                getCreditsMutableLiveData.postValue(handleCreditsResponse(response))
            } else {
                getCreditsMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getCreditsMutableLiveData.postValue(Resource.Error("${t.message}"))
                else -> getCreditsMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleCreditsResponse(response: Response<CreditsResponse>): Resource<CreditsResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { creditsResponse ->
                return Resource.Success(creditsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString("errorMessage")
            }
        }
        return Resource.Error(errorMessage)
    }
}