package com.piyush.nammakathey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class StoryPage(
    val emoji: String,
    val title: String,
    val text: String,
    val bgColor: Int
)

class StoryPageAdapter(private val pages: List<StoryPage>) :
    RecyclerView.Adapter<StoryPageAdapter.StoryPageViewHolder>() {

    class StoryPageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvStoryEmoji)
        val tvTitle: TextView = view.findViewById(R.id.tvStoryPageTitle)
        val tvText: TextView = view.findViewById(R.id.tvStoryPageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story_page, parent, false)
        return StoryPageViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryPageViewHolder, position: Int) {
        val page = pages[position]
        holder.tvEmoji.text = page.emoji
        holder.tvTitle.text = page.title
        holder.tvText.text = page.text
        holder.itemView.setBackgroundColor(page.bgColor)
    }

    override fun getItemCount() = pages.size
}