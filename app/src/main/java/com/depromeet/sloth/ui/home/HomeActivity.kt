package com.depromeet.sloth.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.depromeet.sloth.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.fragmentFactory = SlothFragmentFactory()

        initNavigationEvent()
    }

    private fun initNavigationEvent() {
        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_bottom)
        navigationView.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_today -> changeFragment(TodayFragment::class.java.name)
                    R.id.menu_class -> changeFragment(ClassFragment::class.java.name)
                    R.id.menu_mypage -> changeFragment(MypageFragment::class.java.name)
                }

                true
            }
            this.selectedItemId = R.id.menu_today
        }
    }

    private fun changeFragment(className: String) {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, className)
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

}