package com.depromeet.sloth.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.depromeet.sloth.ui.home.lessonlist.LessonListFragment
import com.depromeet.sloth.ui.home.mypage.MypageFragment
import com.depromeet.sloth.ui.home.today.TodayFragment

class SlothFragmentFactory : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        when (className) {
            TodayFragment::class.java.name -> {
                return TodayFragment()
            }
            LessonListFragment::class.java.name -> {
                return LessonListFragment()
            }
            MypageFragment::class.java.name -> {
                return MypageFragment()
            }
        }

        return super.instantiate(classLoader, className)
    }
}
