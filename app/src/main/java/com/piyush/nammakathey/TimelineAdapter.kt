package com.piyush.nammakathey

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimelineAdapter(private val heroes: List<Hero>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    private var lastPosition = -1

    class TimelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCentury: TextView = view.findViewById(R.id.tvCentury)
        val tvName: TextView = view.findViewById(R.id.tvTimelineName)
        val tvDistrict: TextView = view.findViewById(R.id.tvTimelineDistrict)
        val tvTagline: TextView = view.findViewById(R.id.tvTimelineTagline)
        val tvCategory: TextView = view.findViewById(R.id.tvTimelineCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val hero = heroes[position]

        val centurySuffix = when {
            hero.century % 100 in 11..13 -> "th"
            hero.century % 10 == 1 -> "st"
            hero.century % 10 == 2 -> "nd"
            hero.century % 10 == 3 -> "rd"
            else -> "th"
        }

        holder.tvCentury.text = "${hero.century}$centurySuffix\nCen."
        holder.tvName.text = hero.name
        holder.tvDistrict.text = "📍 ${hero.district}"
        holder.tvTagline.text = "\"${hero.tagline}\""
        holder.tvCategory.text = hero.category

        // Entrance animation
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(
                holder.itemView.context, R.anim.item_animation
            )
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, HeroDetailActivity::class.java)
            intent.putExtra("hero_id", hero.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = heroes.size
}