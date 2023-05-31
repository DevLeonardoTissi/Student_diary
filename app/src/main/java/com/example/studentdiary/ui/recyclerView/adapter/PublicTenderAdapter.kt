package com.example.studentdiary.ui.recyclerView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.studentdiary.databinding.PublicTenderItemBinding
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.model.PublicTender

class PublicTenderAdapter(var onItemClick: (url: String) -> Unit = {}) :
    androidx.recyclerview.widget.ListAdapter<PublicTender, PublicTenderAdapter.PublicTenderViewHolder>(
        diffCallback
    ) {


    inner class PublicTenderViewHolder(private val binding: PublicTenderItemBinding) :
        ViewHolder(binding.root) {


        private lateinit var publicTender: PublicTender

        init {
            itemView.setOnClickListener {
                if (::publicTender.isInitialized) {
                    publicTender.url?.let{
                        onItemClick(it)
                    }
                }
            }
        }


        fun bind(publicTender: PublicTender) {
            this.publicTender = publicTender
            binding.publicTenderItemTextViewName.text = publicTender.name
            binding.publicTenderItemTextViewDescription.text = publicTender.description
            binding.publicTenderItemShapeableImageView.tryLoadImage(publicTender.img)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PublicTenderViewHolder(
            PublicTenderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: PublicTenderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PublicTender>() {
            override fun areItemsTheSame(oldItem: PublicTender, newItem: PublicTender): Boolean {
                return oldItem.id == newItem.id

            }

            override fun areContentsTheSame(oldItem: PublicTender, newItem: PublicTender): Boolean {
                return oldItem == newItem
            }
        }
    }

}