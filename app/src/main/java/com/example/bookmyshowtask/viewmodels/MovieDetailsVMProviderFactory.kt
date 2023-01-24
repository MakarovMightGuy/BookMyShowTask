package com.example.bookmyshowtask.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookmyshowtask.respository.TestRepository

class MovieDetailsVMProviderFactory(
    private val application: Application,
    private val testRepository: TestRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieDetailsViewModel(application, testRepository) as T
    }
}