package com.example.bookmyshowtask.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bookmyshowtask.R
import com.example.bookmyshowtask.activities.FragmentHostActivity
import com.example.bookmyshowtask.activities.SecondActivity
import com.example.bookmyshowtask.adapters.HorizontalAdapter
import com.example.bookmyshowtask.databinding.FragmentMovieDetailsBinding
import com.example.bookmyshowtask.respository.TestRepository
import com.example.bookmyshowtask.utils.Constants.Companion.BASE_URL
import com.example.bookmyshowtask.utils.Constants.Companion.IMAGE_URL_PREFIX
import com.example.bookmyshowtask.utils.Resource
import com.example.bookmyshowtask.viewmodels.MovieDetailsVMProviderFactory
import com.example.bookmyshowtask.viewmodels.MovieDetailsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieDetailsFragment : Fragment() {
    private var _binding: FragmentMovieDetailsBinding? = null
    private lateinit var viewModel: MovieDetailsViewModel
    private val TAG = "MovieDetailsFragment"
    private var actionBar: ActionBar? = null
    private lateinit var horizontalAdapter: HorizontalAdapter

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        actionBar = (requireActivity() as FragmentHostActivity).supportActionBar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testRepository = TestRepository()
        val application = requireActivity().application
        val viewModelProviderFactory =
            MovieDetailsVMProviderFactory(application, testRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[MovieDetailsViewModel::class.java]

        viewModel.getMovieDetails(BASE_URL)
        viewModel.getMovieDetailsMutableLiveData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { resultResponse ->
                        hideProgressBar()
                        hideRetryButton()
                        Log.d(TAG, "onViewCreated: $resultResponse")

                        actionBar?.let {
                            it.title = resultResponse.title
                        }

                        Glide.with(requireContext())
                            .load(IMAGE_URL_PREFIX + resultResponse.backdrop_path)
                            .placeholder(R.drawable.movie_placeholder)
                            .into(binding.ivBackdropPath)

                        binding.tvOverview.text = resultResponse.overview

                        binding.tvRuntime.text = formatTime(resultResponse.runtime)

                        var genres = ""
                        for (i in resultResponse.genres.indices) {
                            if (i == resultResponse.genres.lastIndex) {
                                genres += resultResponse.genres[i].name
                            } else {
                                genres = "${resultResponse.genres[i].name}, " + genres
                            }
                        }
                        binding.tvGenre.text = genres

                        if (resultResponse.adult) {
                            binding.tvAdult.text = "A"
                        } else {
                            binding.tvAdult.text = "UA"
                        }

                        binding.tvReleaseDate.text = formatDate(resultResponse.release_date)
                    }
                }
                is Resource.Error -> {
                    response.message?.let { errorMessage ->
                        hideProgressBar()
                        showRetryButton()
                        Log.e(TAG, "onViewCreated: $errorMessage")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                    hideRetryButton()
                }
            }
        })

        viewModel.getCredits(BASE_URL)
        viewModel.getCreditsMutableLiveData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { resultResponse ->
                        hideProgressBar()
                        hideRetryButton()

                        val actors = resultResponse.cast
                        val crew = resultResponse.crew
                        Log.d(TAG, "onViewCreated: crewList Size - ${crew.size}")

                        if (actors[0].known_for_department == "Acting") {
                            setUpRecyclerView("Acting")
                            horizontalAdapter.castDiffer.submitList(actors)
                        }

                        if (crew[0].known_for_department != "Acting") {
                            setUpRecyclerView("Crew")
                            horizontalAdapter.crewDiffer.submitList(crew)
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let { errorMessage ->
                        hideProgressBar()
                        showRetryButton()
                        Log.e(TAG, "onViewCreated: Credits Response Error")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                    hideRetryButton()
                }
            }
        })

        binding.btnRetry.setOnClickListener {
            viewModel.getMovieDetails(BASE_URL)
            viewModel.getCredits(BASE_URL)
        }

        binding.btnBookTickets.setOnClickListener {
            Intent(requireContext(), SecondActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView(type: String) {
        horizontalAdapter = HorizontalAdapter(type)

        if (type == "Acting") {
            binding.rvCast.apply {
                adapter = horizontalAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }

        if (type == "Crew") {
            binding.rvCrew.apply {
                adapter = horizontalAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.svMovieDetails.visibility = View.INVISIBLE
        binding.btnBookTickets.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.svMovieDetails.visibility = View.VISIBLE
        binding.btnBookTickets.visibility = View.VISIBLE
    }

    private fun hideRetryButton() {
        binding.btnRetry.visibility = View.INVISIBLE
    }

    private fun showRetryButton() {
        binding.btnRetry.visibility = View.VISIBLE
        binding.svMovieDetails.visibility = View.INVISIBLE
        binding.btnBookTickets.visibility = View.INVISIBLE
    }

    private fun formatTime(time: Int): String {
        val hours: Int = time / 60
        val minutes: Int = time % 60
        return "${hours}h ${minutes}min"
    }

    private fun formatDate(date: String): String {
        val today: LocalDate = LocalDate.parse(date)
        return DateTimeFormatter.ofPattern("d MMM, yyyy").format(today)
    }
}