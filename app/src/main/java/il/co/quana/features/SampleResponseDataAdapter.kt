package il.co.quana.features

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import il.co.quana.R
import il.co.quana.model.SampleResponseData
import kotlinx.android.synthetic.main.sample_response_item.view.*

class SampleResponseDataAdapter: ListAdapter<SampleResponseData, RecyclerView.ViewHolder>(SampleResponseDataListDiffCallback()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sample_response_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val headerViewHolder= holder as ItemViewHolder
        val item = getItem(position)
        headerViewHolder.bind(item)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: SampleResponseData) {
            itemView.itemName.text = "Name: ${item.name}"
            itemView.itemPercentage.text = "Percentage: ${item.Percentage}"
        }
    }


    class SampleResponseDataListDiffCallback : DiffUtil.ItemCallback<SampleResponseData>(){
        override fun areItemsTheSame(oldItem: SampleResponseData, newItem: SampleResponseData): Boolean {
            return newItem.name == oldItem.name
        }

        override fun areContentsTheSame(oldItem: SampleResponseData, newItem: SampleResponseData): Boolean {
            return oldItem == newItem
        }

    }
}