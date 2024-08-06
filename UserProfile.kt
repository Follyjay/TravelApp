package com.example.travelapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserProfile : AppCompatActivity() {

    private lateinit var userHolder: LinearLayout
    private lateinit var name: TextView
    private lateinit var email: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        userHolder = findViewById(R.id.llHolder)

        userHolder.setOnClickListener{
            val intent = Intent(this, UpdateDelete::class.java)
            startActivity(intent)
            finish()
        }

    }

}