package com.piyush.nammakathey

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class District(
    val name: String,
    val emoji: String,
    val heroCount: Int,
    val color: String
)

class DistrictAdapter(private val districts: List<District>) :
    RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder>() {

    private var lastPosition = -1

    class DistrictViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvDistrictEmoji)
        val tvName: TextView = view.findViewById(R.id.tvDistrictName)
        val tvCount: TextView = view.findViewById(R.id.tvHeroCount)
        val bg: LinearLayout = view.findViewById(R.id.districtCardBg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_district, parent, false)
        return DistrictViewHolder(view)
    }

    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        val district = districts[position]
        holder.tvEmoji.text = district.emoji
        holder.tvName.text = district.name
        holder.tvCount.text = "${district.heroCount} ${if (district.heroCount == 1) "Hero" else "Stories"}"
        holder.bg.setBackgroundColor(Color.parseColor(district.color))

        // Entrance animation
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(
                holder.itemView.context, R.anim.item_animation
            )
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DistrictHeroesActivity::class.java)
            intent.putExtra("district_name", district.name)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = districts.size
}