package com.piyush.nammakathey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class DistrictMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_district_map)

        val allHeroes = loadHeroes()

        // Group heroes by district and count
        val districtMap = allHeroes.groupBy { it.district }

        // District info with emoji and color (using softer, pastel colors)
        val districtInfo = mapOf(
            "Dharwad" to Pair("⚔️", "#EBF5FF"), // Soft Blue
            "Belagavi" to Pair("👑", "#E9FBE9"), // Soft Green
            "Shivamogga" to Pair("📜", "#F3E5F5"), // Soft Purple
            "Chikkamagaluru" to Pair("🌿", "#E8F5E9"), // Soft Green
            "Bagalkot" to Pair("🕊️", "#FFF4E6"), // Soft Orange
            "Bengaluru" to Pair("🏙️", "#E0F2F1"), // Soft Teal
            "Chitradurga" to Pair("🏰", "#FFF9C4"), // Soft Yellow
            "Mysuru" to Pair("🐯", "#FFF3E0"), // Soft Orange
            "Kalaburagi" to Pair("✊", "#FFEBF0"), // Soft Pink
            "Dakshina Kannada" to Pair("🌊", "#E1F5FE"), // Soft Light Blue
            "Hassan" to Pair("🏛️", "#E8EAF6"), // Soft Indigo
            "Kolar" to Pair("🏛️", "#F1F8E9"), // Soft Light Green
            "Haveri" to Pair("🏛️", "#FFFDE7") // Soft Yellow
        )

        val districts = districtMap.map { (districtName, heroes) ->
            val info = districtInfo[districtName] ?: Pair("🏛️", "#FAFAFA")
            District(
                name = districtName,
                emoji = info.first,
                heroCount = heroes.size,
                color = info.second
            )
        }.sortedBy { it.name }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerDistricts)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = DistrictAdapter(districts)
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}