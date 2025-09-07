package com.example.saix_poc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup each test card
        setupTestCard(
            findViewById(R.id.cardHeightWeight),
            R.drawable.ic_height,
            R.drawable.icon_background_red,
            "Height & Weight",
            "Capture anthropometrics"
        )

        setupTestCard(
            findViewById(R.id.cardVerticalJump),
            R.drawable.ic_arrow_upward,
            R.drawable.icon_background_green,
            "Vertical Jump",
            "Estimate jump height"
        )

        setupTestCard(
            findViewById(R.id.cardShuttleRun),
            R.drawable.ic_transfer_within_a_station,
            R.drawable.icon_background_blue,
            "Shuttle Run",
            "Measure agility & time"
        )

        val sitUpsCard = findViewById<View>(R.id.cardSitUps)
        setupTestCard(
            sitUpsCard,
            R.drawable.ic_fitness_center,
            R.drawable.icon_background_yellow,
            "Sit-ups",
            "Automatic repetition counting"
        )

        setupTestCard(
            findViewById(R.id.cardEndurance),
            R.drawable.ic_directions_run,
            R.drawable.icon_background_orange,
            "Endurance Runs",
            "Track distance/time with GPS"
        )
        
        setupTestCard(
            findViewById(R.id.cardMoreTests),
            R.drawable.ic_more_horiz,
            R.drawable.icon_background_purple,
            "More Tests",
            "Push-ups, plank, etc."
        )

        // Set click listener for the Sit-ups card
        sitUpsCard.setOnClickListener {
            startActivity(Intent(this, SitUpTestActivity::class.java))
        }

        // Handle Bottom Navigation clicks
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> showToast("Home clicked")
                R.id.nav_tests -> showToast("Tests clicked")
                R.id.nav_leaderboard -> showToast("Leaderboard clicked")
                R.id.nav_profile -> showToast("Profile clicked")
            }
            true
        }
    }

    private fun setupTestCard(cardView: View, iconRes: Int, iconBgRes: Int, title: String, subtitle: String) {
        val cardIcon = cardView.findViewById<ImageView>(R.id.card_icon)
        val cardTitle = cardView.findViewById<TextView>(R.id.card_title)
        val cardSubtitle = cardView.findViewById<TextView>(R.id.card_subtitle)

        cardIcon.setImageResource(iconRes)
        cardIcon.background = ContextCompat.getDrawable(this, iconBgRes)
        cardTitle.text = title
        cardSubtitle.text = subtitle
        
        // Set a generic click listener for other cards
        if (cardView.id != R.id.cardSitUps) {
             cardView.setOnClickListener { showToast("$title clicked") }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}