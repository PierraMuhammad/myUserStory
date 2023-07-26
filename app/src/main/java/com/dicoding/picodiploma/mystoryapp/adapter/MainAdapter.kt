package com.dicoding.picodiploma.mystoryapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mystoryapp.DateFormat
import com.dicoding.picodiploma.mystoryapp.R
import com.dicoding.picodiploma.mystoryapp.activity.DetailStoryActivity
import com.dicoding.picodiploma.mystoryapp.data.response.ListStoryItem

class MainAdapter : PagingDataAdapter<ListStoryItem, MainAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.avatar_name)
        val imgPhoto: ImageView = view.findViewById(R.id.picture)
        val itemDesc: TextView = view.findViewById(R.id.tv_item_description)
        val itemDate: TextView = view.findViewById(R.id.tv_item_created)
        val maxLength = 50

        fun bind(list: ListStoryItem){
            val displayedText = if (list.description.length > maxLength){
                list.description.substring(0, maxLength) + "..."
            } else {
                list.description
            }
            Glide.with(itemView.context).load(list.photoUrl).into(imgPhoto)
            itemName.text = list.name
            itemDesc.text = displayedText
            itemDate.text = "Tanggal dibuat: ${list.createdAt.DateFormat()}"
            itemView.setOnClickListener {
                val intentToDetail = Intent(itemView.context, DetailStoryActivity::class.java)
                intentToDetail.putExtra(DetailStoryActivity.NAME, list.name)
                intentToDetail.putExtra(DetailStoryActivity.CREATE_AT, list.createdAt)
                intentToDetail.putExtra(DetailStoryActivity.DESCRIPTION, list.description)
                intentToDetail.putExtra(DetailStoryActivity.PHOTO_URL, list.photoUrl)

                itemView.context.startActivity(intentToDetail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null){
            holder.bind(data)
        }
    }
}