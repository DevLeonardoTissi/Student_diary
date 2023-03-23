package com.example.studentdiary.ui.recyclerView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.studentdiary.databinding.DisciplineItemBinding
import com.example.studentdiary.model.Discipline

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
            binding.disciplineItemName.text = discipline.name
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
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Discipline, newItem: Discipline): Boolean {
                return oldItem.name == newItem.name
            }

        }
    }


}