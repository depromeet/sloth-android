package com.depromeet.sloth.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.depromeet.sloth.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<BottomNavigationView>(R.id.bottom_navi).setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_today -> {

                }
                R.id.menu_class -> {

                }
                R.id.menu_mypage -> {

                }
            }

            true
        }
    }
}