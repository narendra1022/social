package com.example.dummy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportFragmentManager.beginTransaction()
            .add(R.id.prof, ProfileFragment()).commit()
    }
}
