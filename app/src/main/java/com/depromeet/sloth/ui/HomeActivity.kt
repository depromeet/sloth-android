package com.depromeet.sloth.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.ui.list.ListFragment
import com.depromeet.sloth.ui.manage.ManageFragment
import com.depromeet.sloth.ui.list.TodayFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.fragmentFactory = SlothFragmentFactory()
        initNavigationEvent()
    }

    private fun initNavigationEvent() {
        binding.navigationView.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_today -> changeFragment(TodayFragment::class.java.name)
                    R.id.menu_class -> changeFragment(ListFragment::class.java.name)
                    R.id.menu_mypage -> changeFragment(ManageFragment::class.java.name)
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