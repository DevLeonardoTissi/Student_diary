package com.example.studentdiary.ui.recyclerView.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.studentdiary.R
import com.example.studentdiary.databinding.DisciplineItemBinding
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.utils.concatUtils.concatenateDateValues
import com.example.studentdiary.utils.concatUtils.concatenateTimeValues

class DisciplineListAdapter(
    var onItemClick: (disciplineId: String) -> Unit = {},
    var onClickingOnOptionDetails: (disciplineId: String) -> Unit = {},
    var onClickingOnOptionDelete: (disciplineId: String) -> Unit = {},
    var onClickingOnOptionEdit: (disciplineId: String) -> Unit = {},
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

            itemView.setOnLongClickListener {
                PopupMenu(binding.root.context, itemView, Gravity.END).apply {
                    menuInflater.inflate(R.menu.on_long_click_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        item?.let {
                            when (it.itemId) {
                                R.id.menuPopup_MenuItem_details -> {
                                    onClickingOnOptionDetails(discipline.id)
                                }

                                R.id.menuPopup_MenuItem_edit -> {
                                    onClickingOnOptionEdit(discipline.id)
                                }

                                R.id.menuPopup_MenuItem_remove -> {
                                    onClickingOnOptionDelete(discipline.id)
                                }

                                else -> {}
                            }
                        }
                        true
                    }
                }.show()
                true
            }
        }

        fun bind(discipline: Discipline) {
            this.discipline = discipline
            binding.disciplineItemTextViewName.text = discipline.name

            binding.disciplineItemImageViewFavorite.apply {
                visibility = if (discipline.favorite) View.VISIBLE else View.GONE

            }

            binding.disciplineItemImageViewCompleted.apply {
                visibility = if (discipline.completed) View.VISIBLE else View.GONE
            }

            binding.disciplineItemShapeableImageView.apply {
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
                    this.text = concatenateDateValues(it)
                } ?: kotlin.run {
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