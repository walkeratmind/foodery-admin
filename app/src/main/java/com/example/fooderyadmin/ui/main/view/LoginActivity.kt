package com.example.fooderyadmin.ui.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.fooderyadmin.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        registerTextView.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        })

    }
}