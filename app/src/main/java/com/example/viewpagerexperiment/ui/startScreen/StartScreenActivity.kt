package com.example.viewpagerexperiment.ui.startScreen

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.viewpagerexperiment.MainActivity
import com.example.viewpagerexperiment.R

class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_start_screen)

        object : CountDownTimer(1000, 1000) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                startActivity(Intent(this@StartScreenActivity, MainActivity::class.java))
                finish()
            }


        }.start()

    }
}