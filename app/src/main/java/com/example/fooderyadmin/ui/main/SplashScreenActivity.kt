package com.example.fooderyadmin.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fooderyadmin.ui.main.view.LoginActivity
import com.example.fooderyadmin.ui.main.view.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
//        setContentView(R.layout.activity_splash_screen)
    }
}