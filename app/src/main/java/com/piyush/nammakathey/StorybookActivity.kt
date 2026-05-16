package com.piyush.nammakathey

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.util.Locale

class StorybookActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsContainer: LinearLayout
    private lateinit var tvPageCount: TextView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var btnSpeak: Button
    private lateinit var pages: List<StoryPage>
    private lateinit var tts: TextToSpeech
    private var isSpeaking = false
    private var isKannada = false

    private val bgColors = listOf(
        Color.parseColor("#1F4E79"),
        Color.parseColor("#1B5E20"),
        Color.parseColor("#4A148C"),
        Color.parseColor("#BF360C"),
        Color.parseColor("#004D40"),
        Color.parseColor("#1A237E")
    )

    private val emojis = mapOf(
        "Freedom Fighter" to listOf("⚔️", "🏰", "🦁", "🔥", "🎌"),
        "Poet" to listOf("📜", "✍️", "🌸", "🎭", "📖"),
        "Social Reformer" to listOf("✊", "🕊️", "⚖️", "🌟", "🙏")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storybook)

        val heroId = intent.getIntExtra("hero_id", 1)
        isKannada = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("is_kannada", false)

        val heroes = loadHeroes()
        val hero = heroes.find { it.id == heroId } ?: return

        viewPager = findViewById(R.id.viewPagerStory)
        dotsContainer = findViewById(R.id.dotsContainer)
        tvPageCount = findViewById(R.id.tvPageCount)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        btnSpeak = findViewById(R.id.btnSpeakStory)

        // TTS Setup
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = if (isKannada) Locale("kn", "IN") else Locale.ENGLISH
            }
        }

        btnSpeak.setOnClickListener {
            if (isSpeaking) {
                stopSpeaking()
            } else {
                speakCurrentPage()
            }
        }

        findViewById<TextView>(R.id.tvStorybookTitle).text =
            if (isKannada) hero.nameKannada else hero.name

        // Split story into pages
        val storyText = if (isKannada) hero.storyKannada else hero.story
        pages = splitIntoPages(storyText, hero.category, isKannada, hero)

        val adapter = StoryPageAdapter(pages)
        viewPager.adapter = adapter

        setupDots()
        updateNavButtons(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tvPageCount.text = "${position + 1}/${pages.size}"
                updateDots(position)
                updateNavButtons(position)
                if (isSpeaking) {
                    speakCurrentPage()
                }
            }
        })

        btnPrev.setOnClickListener {
            if (viewPager.currentItem > 0)
                viewPager.currentItem = viewPager.currentItem - 1
        }

        btnNext.setOnClickListener {
            if (viewPager.currentItem < pages.size - 1)
                viewPager.currentItem = viewPager.currentItem + 1
            else
                finish()
        }
    }

    private fun speakCurrentPage() {
        val currentPage = pages[viewPager.currentItem]
        val textToSpeak = "${currentPage.title}. ${currentPage.text}"

        if (isKannada) {
            val result = tts.setLanguage(Locale("kn", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Kannada voice not supported on this device", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            tts.language = Locale.ENGLISH
        }

        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "page_speech")
        isSpeaking = true
        btnSpeak.text = "⏹"
    }

    private fun stopSpeaking() {
        tts.stop()
        isSpeaking = false
        btnSpeak.text = "🔊"
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private fun splitIntoPages(
        story: String,
        category: String,
        isKannada: Boolean,
        hero: Hero
    ): List<StoryPage> {
        val sentences = story.split(". ").filter { it.isNotBlank() }
        val chunkSize = 2
        val chunks = sentences.chunked(chunkSize) { it.joinToString(". ") + "." }

        val categoryEmojis = emojis[category] ?: emojis["Freedom Fighter"]!!
        val pages = mutableListOf<StoryPage>()

        // Cover page
        pages.add(StoryPage(
            emoji = categoryEmojis[0],
            title = if (isKannada) "— ${hero.nameKannada} —" else "— ${hero.name} —",
            text = if (isKannada) hero.taglineKannada else hero.tagline,
            bgColor = bgColors[0]
        ))

        // Story pages
        chunks.forEachIndexed { index, chunk ->
            pages.add(StoryPage(
                emoji = categoryEmojis[(index + 1) % categoryEmojis.size],
                title = if (isKannada) "ಅಧ್ಯಾಯ ${index + 1}" else "Chapter ${index + 1}",
                text = chunk,
                bgColor = bgColors[(index + 1) % bgColors.size]
            ))
        }

        // End page
        pages.add(StoryPage(
            emoji = "🏅",
            title = if (isKannada) "ಸಮಾಪ್ತಿ" else "The End",
            text = if (isKannada)
                "ಈ ವೀರರ ಕಥೆ ನಮಗೆ ಸ್ಫೂರ್ತಿ ನೀಡಲಿ! 🙏"
            else
                "May this hero's story inspire us all! 🙏",
            bgColor = bgColors[5]
        ))

        return pages
    }

    private fun setupDots() {
        dotsContainer.removeAllViews()
        pages.forEachIndexed { index, _ ->
            val dot = TextView(this)
            dot.text = if (index == 0) "●" else "○"
            dot.textSize = 16f
            dot.setTextColor(Color.WHITE)
            dot.setPadding(4, 0, 4, 0)
            dotsContainer.addView(dot)
        }
        tvPageCount.text = "1/${pages.size}"
    }

    private fun updateDots(position: Int) {
        for (i in 0 until dotsContainer.childCount) {
            val dot = dotsContainer.getChildAt(i) as TextView
            dot.text = if (i == position) "●" else "○"
        }
    }

    private fun updateNavButtons(position: Int) {
        btnPrev.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        btnNext.text = if (position == pages.size - 1) "Finish 🎉" else "Next ▶"
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}