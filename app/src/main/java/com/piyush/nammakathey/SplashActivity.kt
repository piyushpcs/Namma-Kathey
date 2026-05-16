package com.piyush.nammakathey

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val title = findViewById<TextView>(R.id.tvSplashTitle)
        val subtitle = findViewById<TextView>(R.id.tvSplashSubtitle)
        val tagline = findViewById<TextView>(R.id.tvSplashTagline)

        // Animate title
        title.animate()
            .alpha(1f)
            .translationYBy(-40f)
            .setDuration(800)
            .setStartDelay(200)
            .start()

        // Animate subtitle
        subtitle.animate()
            .alpha(1f)
            .translationYBy(-20f)
            .setDuration(800)
            .setStartDelay(600)
            .start()

        // Animate tagline
        tagline.animate()
            .alpha(1f)
            .translationYBy(-20f)
            .setDuration(800)
            .setStartDelay(900)
            .withEndAction {
                // Go to MainActivity after animation
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            .start()
    }
}