package com.depromeet.sloth.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class SlothFragmentFactory : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        when (className) {
            TodayFragment::class.java.name -> {
                return TodayFragment()
            }
            ClassFragment::class.java.name -> {
                return ClassFragment()
            }
            MypageFragment::class.java.name -> {
                return MypageFragment()
            }
        }

        return super.instantiate(classLoader, className)
    }
}
