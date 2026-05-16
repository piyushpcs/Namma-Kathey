package com.piyush.nammakathey

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.util.Locale

class HeroDetailActivity : AppCompatActivity() {

    private lateinit var hero: Hero
    private lateinit var btnBookmark: Button
    private lateinit var btnSpeak: Button
    private lateinit var tts: TextToSpeech
    private var isSpeaking = false
    private var isKannada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hero_detail)

        val heroId = intent.getIntExtra("hero_id", 1)
        isKannada = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("is_kannada", false)
        val heroes = loadHeroes()
        hero = heroes.find { it.id == heroId } ?: return

        btnBookmark = findViewById(R.id.btnBookmark)
        btnSpeak = findViewById(R.id.btnSpeak)

        updateUI()
        updateBookmarkButton()

        // TTS Setup
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = if (isKannada) Locale("kn") else Locale.ENGLISH
            }
        }

        // Listen button
        btnSpeak.setOnClickListener {
            if (isSpeaking) {
                tts.stop()
                isSpeaking = false
                btnSpeak.text = "🔊 Listen"
            } else {
                val audioManager = getSystemService(AUDIO_SERVICE) as android.media.AudioManager
                audioManager.setStreamVolume(
                    android.media.AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC),
                    0
                )
                val text = if (isKannada) hero.storyKannada else hero.story
                if (isKannada) {
                    val kannadaResult = tts.setLanguage(Locale("kn", "IN"))
                    if (kannadaResult == TextToSpeech.LANG_MISSING_DATA ||
                        kannadaResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.language = Locale.ENGLISH
                        Toast.makeText(this,
                            "Kannada voice not available, playing in English",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    tts.language = Locale.ENGLISH
                }
                val params = android.os.Bundle()
                params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "story")
                isSpeaking = true
                btnSpeak.text = "⏹ Stop"
            }
        }

        // Bookmark
        btnBookmark.setOnClickListener { toggleBookmark() }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Share
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            val shareText = "🌟 ${hero.name} — ${hero.district}\n\n" +
                    "\"${hero.tagline}\"\n\n${hero.story}\n\n" +
                    "Shared from Namma Kathey 📖"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(intent, "Share Hero Story"))
        }

        // Read as Storybook
        findViewById<Button>(R.id.btnReadAsStory).setOnClickListener {
            val intent = Intent(this, StorybookActivity::class.java)
            intent.putExtra("hero_id", hero.id)
            startActivity(intent)
        }

        // Find Statue
        findViewById<Button>(R.id.btnFindStatue).setOnClickListener {
            if (hero.memorialLat != 0.0 && hero.memorialLng != 0.0) {
                val uri = android.net.Uri.parse(
                    "geo:${hero.memorialLat},${hero.memorialLng}?q=${hero.memorialLat},${hero.memorialLng}(${hero.memorialName})"
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    val browserUri = android.net.Uri.parse(
                        "https://maps.google.com/?q=${hero.memorialLat},${hero.memorialLng}"
                    )
                    startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                }
            } else {
                Toast.makeText(
                    this,
                    "Memorial location not available for this hero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateUI() {
        if (isKannada) {
            findViewById<TextView>(R.id.tvDetailName).text = hero.nameKannada
            findViewById<TextView>(R.id.tvDetailTagline).text = "\"${hero.taglineKannada}\""
            findViewById<TextView>(R.id.tvDetailStory).text = hero.storyKannada
        } else {
            findViewById<TextView>(R.id.tvDetailName).text = hero.name
            findViewById<TextView>(R.id.tvDetailTagline).text = "\"${hero.tagline}\""
            findViewById<TextView>(R.id.tvDetailStory).text = hero.story
        }
        findViewById<TextView>(R.id.tvDetailDistrict).text = "📍 ${hero.district}"
        findViewById<TextView>(R.id.tvDetailCategory).text = hero.category
    }

    private fun toggleBookmark() {
        val prefs = getSharedPreferences("bookmarks", MODE_PRIVATE)
        val key = "bookmark_${hero.id}"
        val isBookmarked = prefs.getBoolean(key, false)
        prefs.edit().putBoolean(key, !isBookmarked).apply()
        updateBookmarkButton()
        val msg = if (!isBookmarked) "Bookmarked! 🔖" else "Bookmark removed"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun updateBookmarkButton() {
        val prefs = getSharedPreferences("bookmarks", MODE_PRIVATE)
        val isBookmarked = prefs.getBoolean("bookmark_${hero.id}", false)
        btnBookmark.text = if (isBookmarked) "🔖 Saved!" else "🔖 Bookmark"
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}