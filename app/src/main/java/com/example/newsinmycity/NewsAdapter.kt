package com.example.newsinmycity

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class NewsAdapter(
    private val newsList: MutableList<NewsItem>,
    private val onLikeClick: (NewsItem, Int) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    fun removeItem(position: Int) {
        if (position >= 0 && position < newsList.size) {
            newsList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, newsList.size)
        }
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val sourceTextView: TextView = itemView.findViewById(R.id.sourceTextView)
        val newsImageView: ImageView = itemView.findViewById(R.id.newsImageView)
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]

        holder.titleTextView.text = newsItem.title

        // Показываем описание если есть
        if (newsItem.description.isNotEmpty()) {
            holder.descriptionTextView.text = newsItem.description
            holder.descriptionTextView.visibility = View.VISIBLE
        } else {
            holder.descriptionTextView.visibility = View.GONE
        }

        // Показываем категорию
        holder.categoryTextView.text = newsItem.category

        // Показываем источник
        holder.sourceTextView.text = getSourceDisplayName(newsItem.source)

        // Отображаем дату и количество просмотров
        val dateWithViews = if (newsItem.viewCount > 0) {
            "${newsItem.publishedAt} • ${formatViewCount(newsItem.viewCount)} просмотров"
        } else {
            newsItem.publishedAt
        }
        holder.dateTextView.text = dateWithViews

        // Показываем изображение с плавным переходом
        if (!newsItem.imageUrl.isNullOrEmpty()) {
            holder.newsImageView.visibility = View.VISIBLE

            Glide.with(holder.itemView.context)
                .load(newsItem.imageUrl)
                .error(R.drawable.ic_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(holder.newsImageView)
        } else {
            // Если нет изображения, показываем placeholder
            holder.newsImageView.visibility = View.VISIBLE
            holder.newsImageView.setImageResource(R.drawable.ic_news_placeholder)
        }

        // Настройка кнопки лайка с анимацией
        updateLikeButton(holder.likeButton, newsItem.isLiked)
        holder.likeButton.setOnClickListener {
            // Добавляем анимацию нажатия
            it.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()

            onLikeClick(newsItem, position)
        }

        // Клик по новости
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.sourceUrl))
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun getSourceDisplayName(source: NewsSource): String {
        return when (source.type) {
            SourceType.TELEGRAM -> source.name
            SourceType.RSS -> source.name
            else -> source.name
        }
    }

    private fun updateLikeButton(button: ImageButton, isLiked: Boolean) {
        // Используем кастомные иконки сердечек
        val iconRes = if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        button.setImageResource(iconRes)
    }

    fun updateLike(position: Int, isLiked: Boolean) {
        if (position >= 0 && position < newsList.size) {
            val originalItem = newsList[position]
            // Создаем новый объект NewsItem с обновленным состоянием лайка
            val updatedItem = NewsItem(
                id = originalItem.id,
                title = originalItem.title,
                description = originalItem.description,
                imageUrl = originalItem.imageUrl,
                sourceUrl = originalItem.sourceUrl,
                publishedAt = originalItem.publishedAt,
                viewCount = originalItem.viewCount,
                category = originalItem.category,
                source = originalItem.source,
                isLiked = isLiked
            )
            newsList[position] = updatedItem
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = newsList.size

    fun updateNews(newNews: List<NewsItem>) {
        newsList.clear()
        newsList.addAll(newNews)
        notifyDataSetChanged()
    }

    private fun formatViewCount(count: Int): String {
        return when {
            count >= 1000000 -> "${count / 1000000}.${(count % 1000000) / 100000} млн"
            count >= 1000 -> "${count / 1000}.${(count % 1000) / 100} тыс"
            else -> count.toString()
        }
    }
}
