package com.piyush.nammakathey

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HeroAdapter(private var heroes: List<Hero>, private val isKannada: Boolean = false) :
    RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    private var lastPosition = -1

    class HeroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvHeroName)
        val tvDistrict: TextView = view.findViewById(R.id.tvHeroDistrict)
        val tvIcon: TextView = view.findViewById(R.id.tvHeroIcon)
        val tvCategory: TextView = view.findViewById(R.id.tvHeroCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hero, parent, false)
        return HeroViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val hero = heroes[position]

        holder.tvName.text = if (isKannada) hero.nameKannada else hero.name
        holder.tvDistrict.text = hero.district
        holder.tvCategory.text = hero.category

        val icon = when (hero.category) {
            "Freedom Fighter" -> "🦁"
            "Poet" -> "📜"
            "Social Reformer" -> "✊"
            else -> "🏛️"
        }
        holder.tvIcon.text = icon

        // Apply soft background to icon
        val iconBg = when (hero.category) {
            "Freedom Fighter" -> "#E3F2FD" // blue
            "Poet" -> "#FCE4EC" // pink
            "Social Reformer" -> "#E8F5E9" // green
            else -> "#FFF3E0" // orange
        }
        holder.tvIcon.backgroundTintList = android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor(iconBg))

        // Card background should be white
        val card = holder.itemView as androidx.cardview.widget.CardView
        card.setCardBackgroundColor(android.graphics.Color.WHITE)

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

    fun updateList(filtered: List<Hero>) {
        heroes = filtered
        lastPosition = -1
        notifyDataSetChanged()
    }
}