package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.results_page.*

@SuppressLint("Registered")
class ResultsPage: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.results_page)

        userButton.setOnClickListener {
            startActivity(Intent(this, AccountPage::class.java))
        }

        val dropDownList = arrayOf("Sort By:", "Option 1", "Option 2", "Option 3", "Option 4");
        val attachArray = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropDownList);

        dropDown.adapter = attachArray;
    }
}