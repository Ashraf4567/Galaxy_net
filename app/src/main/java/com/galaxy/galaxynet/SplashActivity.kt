package com.galaxy.galaxynet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper())
            .postDelayed({
                startMainActivity()
            },2000)
    }

    private fun startMainActivity() {
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}