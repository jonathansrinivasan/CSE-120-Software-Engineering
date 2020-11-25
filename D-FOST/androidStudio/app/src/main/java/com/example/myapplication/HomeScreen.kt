package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.home_page.*

class HomeScreen: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        userButton.setOnClickListener {
            startActivity(Intent(this, AccountPage::class.java))
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, ResultsPage::class.java))
        }
    }
}