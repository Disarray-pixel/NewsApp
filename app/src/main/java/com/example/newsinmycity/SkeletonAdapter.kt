package com.example.newsinmycity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SkeletonAdapter : RecyclerView.Adapter<SkeletonAdapter.SkeletonViewHolder>() {

    // Показываем 5 skeleton элементов
    private val itemCount = 5

    class SkeletonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news_skeleton, parent, false)
        return SkeletonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {
        // Skeleton элементы не требуют настройки
    }

    override fun getItemCount(): Int = itemCount
}
