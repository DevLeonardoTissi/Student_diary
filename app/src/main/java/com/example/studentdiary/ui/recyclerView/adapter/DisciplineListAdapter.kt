package com.example.studentdiary.ui.recyclerView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.studentdiary.databinding.DisciplineItemBinding
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.utils.concatenateDateValues
import com.example.studentdiary.utils.concatenateTimeValues

class DisciplineListAdapter(
    var onItemClick: (disciplineId: String) -> Unit = {}
) : androidx.recyclerview.widget.ListAdapter<Discipline, DisciplineListAdapter.DisciplineViewHolder>(
    diffCallback
) {


    inner class DisciplineViewHolder(
        private val binding: DisciplineItemBinding
    ) : ViewHolder(binding.root) {

        private lateinit var discipline: Discipline

        init {
            itemView.setOnClickListener {
                if (::discipline.isInitialized) {
                    onItemClick(discipline.id)
                }
            }
        }

        fun bind(discipline: Discipline) {
            this.discipline = discipline
            binding.disciplineItemTextViewName.text = discipline.name

            binding.disciplineItemImageViewFavorite.apply {
                visibility = if (discipline.favorite) View.VISIBLE else View.GONE

            }

            binding.disciplineItemImageViewCompleted.apply {
                visibility = if (discipline.completed)  View.VISIBLE else View.GONE
            }

            binding.disciplineItemShapeableImageView.apply {
                    visibility = if (discipline.img == null) View.GONE else View.VISIBLE
                    tryLoadImage(discipline.img)
            }

            discipline.startTime?.let {
                binding.disciplineItemTextViewStartTime.text = concatenateTimeValues(it)
            }

            discipline.endTime?.let {
                binding.disciplineItemTextViewEndTime.text = concatenateTimeValues(it)
            }

            binding.disciplineItemTextViewDate.apply {
                discipline.date?.let {
                    this.visibility = View.VISIBLE
                    this.text= concatenateDateValues(it)
                }?: kotlin.run {
                    visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplineViewHolder =
        DisciplineViewHolder(
            DisciplineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: DisciplineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Discipline>() {
            override fun areItemsTheSame(oldItem: Discipline, newItem: Discipline): Boolean {
                return oldItem.id == newItem.id

            }

            override fun areContentsTheSame(oldItem: Discipline, newItem: Discipline): Boolean {
                return oldItem == newItem
            }
        }
    }


}