package com.oliversolutions.dev.bloodpressurediary.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oliversolutions.dev.bloodpressurediary.databinding.BloodPressureViewItemBinding
import com.oliversolutions.dev.bloodpressurediary.databinding.HeaderBinding
import com.oliversolutions.dev.bloodpressurediary.databinding.SubHeaderBinding

const val ITEM_VIEW_TYPE_HEADER = 0
const val ITEM_VIEW_TYPE_ITEM = 1
const val ITEM_VIEW_TYPE_SUB_HEADER = 2

class BloodPressureGridAdapter(private val onClickListener: OnClickListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (bloodPressure: BloodPressure) -> Unit) {
        fun onClick(bloodPressure: BloodPressure) = clickListener(bloodPressure)
    }

    class HighPressureViewHolder(private var binding: BloodPressureViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bloodPressure: BloodPressure) {
            binding.bloodPressure = bloodPressure
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): HighPressureViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BloodPressureViewItemBinding.inflate(layoutInflater, parent, false)
                return HighPressureViewHolder(binding)
            }
        }
    }

    class HeaderViewHolder(private var binding: HeaderBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(fromDate: String, toDate: String, numRecords: Int) {
            binding.fromDate = fromDate
            binding.toDate = toDate
            binding.numRecords = numRecords.toString()

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }

    class SubHeaderViewHolder(private var binding: SubHeaderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(date:String, averageSystolic: String, averageDiastolic: String, averagePulse: String) {
            binding.averageSystolic = averageSystolic
            binding.averageDiastolic = averageDiastolic
            binding.averagePulse = averagePulse
            binding.date = date

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SubHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SubHeaderBinding.inflate(layoutInflater, parent, false)
                return SubHeaderViewHolder(binding)
            }
        }
    }


    fun addHeaderAndSubmitList(list: List<BloodPressure>, fromDate: String, toDate: String) {
        val items = mutableListOf<DataItem>()
        var newDate = ""
        items += listOf(DataItem.Header(fromDate, toDate, list.size))
        for (highPressureItem in list) {
            if (newDate.isEmpty() || newDate != highPressureItem.creationDate) {
                newDate = highPressureItem.creationDate!!
                items += listOf(DataItem.SubHeader(newDate, highPressureItem.averageSystolic!!, highPressureItem.averageDiastolic!!, highPressureItem.averagePulse!!))
            }
            items += listOf(DataItem.HighPressureItem(highPressureItem))
        }
        submitList(items)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.HighPressureItem -> ITEM_VIEW_TYPE_ITEM
            is DataItem.SubHeader -> ITEM_VIEW_TYPE_SUB_HEADER

        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_SUB_HEADER -> SubHeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> HighPressureViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HighPressureViewHolder -> {
                val highPressureItem = getItem(position) as DataItem.HighPressureItem
                holder.bind(highPressureItem.bloodPressure)
                holder.itemView.setOnClickListener {
                    onClickListener.onClick(highPressureItem.bloodPressure)
                }
            }
            is SubHeaderViewHolder -> {
                val subHeaderItem = getItem(position) as DataItem.SubHeader
                holder.bind(subHeaderItem.date, subHeaderItem.averageSystolic, subHeaderItem.averageDiastolic, subHeaderItem.averagePulse)
            }
            is HeaderViewHolder -> {
                val headerDataItem = getItem(position) as DataItem.Header
                holder.bind(headerDataItem.fromDate, headerDataItem.toDate, headerDataItem.numRecords)
            }
        }
    }
}

sealed class DataItem {

    data class HighPressureItem(val bloodPressure: BloodPressure): DataItem() {
        override val id: Long = bloodPressure.id
    }

    data class Header(val fromDate: String, val toDate: String, val numRecords: Int): DataItem() {
        override val id = Long.MIN_VALUE
    }

    data class SubHeader(val date: String, val averageSystolic: String, val averageDiastolic: String, val averagePulse: String): DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}

