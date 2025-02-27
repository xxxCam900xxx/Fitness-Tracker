package com.example.heartspiek

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    // Splash Variable
    private lateinit var splashText: TextView
    private var splashTime: Long = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashText = findViewById(R.id.splash_text)
        val text = "YOUR LIFE IS A GAME"
        val spannableString = SpannableString(text)

        // "LIFE" (Position 6-10)
        spannableString.setSpan(ForegroundColorSpan(Color.RED), 5, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        // "GAME" (Position 15-19)
        spannableString.setSpan(ForegroundColorSpan(Color.RED), 15, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        splashText.text = spannableString

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashTime)
    }
}