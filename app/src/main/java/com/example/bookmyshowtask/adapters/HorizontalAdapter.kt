package com.example.bookmyshowtask.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmyshowtask.R
import com.example.bookmyshowtask.databinding.ItemHorizontalBinding
import com.example.bookmyshowtask.models.credits.Cast
import com.example.bookmyshowtask.models.credits.Crew
import com.example.bookmyshowtask.utils.Constants.Companion.IMAGE_URL_PREFIX

class HorizontalAdapter(private val type: String) :
    RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder>() {

    inner class HorizontalViewHolder(val binding: ItemHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val castDifferCallback = object : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem == newItem
        }

    }

    private val crewDifferCallback = object : DiffUtil.ItemCallback<Crew>() {
        override fun areItemsTheSame(oldItem: Crew, newItem: Crew): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crew, newItem: Crew): Boolean {
            return oldItem == newItem
        }

    }

    val castDiffer = AsyncListDiffer(this, castDifferCallback)
    val crewDiffer = AsyncListDiffer(this, crewDifferCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val binding =
            ItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorizontalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (type == "Acting") {
            castDiffer.currentList.size
        } else {
            crewDiffer.currentList.size
        }
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        holder.binding.apply {
            if (type == "Acting") {
                val castItem = castDiffer.currentList[position]
                Glide.with(root).load(IMAGE_URL_PREFIX + castItem.profile_path)
                    .placeholder(R.drawable.person_placeholder)
                    .into(ivProfilePath)

                tvOriginalName.text = castItem.original_name

                val characterNamesList = castItem.character.split("/")
                val characterName: String = if (characterNamesList.size > 1) {
                    characterNamesList[0]
                } else {
                    castItem.character
                }
                tvCharacter.text = "as $characterName"
            }

            if (type == "Crew") {
                val crewItem = crewDiffer.currentList[position]
                Glide.with(root).load(IMAGE_URL_PREFIX + crewItem.profile_path)
                    .placeholder(R.drawable.person_placeholder)
                    .into(ivProfilePath)

                tvOriginalName.text = crewItem.original_name

                val characterNamesList = crewItem.job.split("/")
                val characterName: String = if (characterNamesList.size > 1) {
                    characterNamesList[0]
                } else {
                    crewItem.job
                }
                tvCharacter.text = characterName
            }
        }
    }
}