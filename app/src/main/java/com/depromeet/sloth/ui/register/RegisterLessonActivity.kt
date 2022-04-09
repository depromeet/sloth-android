package com.depromeet.sloth.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.databinding.ActivityRegisterLessonBinding
import com.depromeet.sloth.extensions.handleLoadingState
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterLessonActivity : BaseActivity<ActivityRegisterLessonBinding>() {

    private val viewModel: RegisterLessonViewModel by viewModels()

    lateinit var lessonCategoryMap: HashMap<Int, String>
    internal var lessonCategoryList: MutableList<String> = mutableListOf()

    lateinit var lessonSiteMap: HashMap<Int, String>
    internal var lessonSiteList: MutableList<String> = mutableListOf()

    lateinit var registerLessonFirstFragment: Fragment
    lateinit var registerLessonSecondFragment: Fragment
    lateinit var registerLessonCheckFragment: Fragment
    lateinit var currentFragment: Fragment

    companion object {
        fun newIntent(activity: Activity) =
            Intent(activity, RegisterLessonActivity::class.java)
    }

    override fun getActivityBinding(): ActivityRegisterLessonBinding =
        ActivityRegisterLessonBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerLessonFirstFragment = RegisterLessonFirstFragment()
        registerLessonSecondFragment = RegisterLessonSecondFragment()
        registerLessonCheckFragment = RegisterLessonCheckFragment()
    }

    override fun onStart() {
        super.onStart()

        mainScope {
            initLessonCategory()
            initLessonSite()
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        initViews()
//    }

    override fun initViews() = with(binding) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_register_lesson, registerLessonFirstFragment)
            addToBackStack(null)
            commit()
        }

        tbRegisterLesson.setNavigationOnClickListener { onBackPressed() }
    }

    fun changeFragment(
        fragment: Fragment,
        args: Bundle? = null,
        backPressed: Boolean? = false,
    ) {
        if (args != null) {
            fragment.arguments = args
        }
        if (backPressed == true) {
            supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.slide_left_enter, R.anim.slide_left_exit)
                replace(R.id.fl_register_lesson, fragment)
                addToBackStack(null)
                commit()
            }
        } else {
            supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.slide_right_enter, R.anim.slide_right_exit)
                replace(R.id.fl_register_lesson, fragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onBackPressed() {
        currentFragment = supportFragmentManager.findFragmentById(R.id.fl_register_lesson)!!

        when (currentFragment) {
            is RegisterLessonFirstFragment -> finish()

            is RegisterLessonSecondFragment ->
                changeFragment(registerLessonFirstFragment, null, true)

            is RegisterLessonCheckFragment ->
                changeFragment(registerLessonSecondFragment, null, true)
        }
    }

    private suspend fun initLessonCategory() {
        viewModel.fetchLessonCategoryList().let {
            when (it) {
                is LessonState.Loading -> {
                    showProgress(this)
                }

                is LessonState.Success<List<LessonCategoryResponse>> -> {
                    Log.d("fetch Success", "${it.data}")
                    setLessonCategoryList(it.data)
                }

                is LessonState.Unauthorized -> showLogoutDialog()

                is LessonState.NotFound, LessonState.Forbidden -> {
                    Toast.makeText(this, "강의 카테고리 정보를 가져오지 못했어요", Toast.LENGTH_SHORT).show()
                }

                is LessonState.Error -> {
                    Log.d("fetch Error", "${it.exception}")
                    Toast.makeText(this, "강의 카테고리 정보를 가져오지 못했어요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLogoutDialog() {
        val dlg = SlothDialog(this, DialogState.FORBIDDEN)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                logout()
            }
        }
        dlg.start()
    }

    private fun logout() {
        viewModel.removeAuthToken()
        Toast.makeText(this, "로그아웃 되었어요", Toast.LENGTH_SHORT).show()
        startActivity(LoginActivity.newIntent(this))
    }

    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) = with(binding)
    {
        lessonCategoryMap =
            data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
        lessonCategoryList = data.map { it.categoryName }.toMutableList()
        lessonCategoryList.add(0, "강의 카테고리를 선택해 주세요")
    }

    private suspend fun initLessonSite() {
        viewModel.fetchLessonSiteList().let {
            when (it) {
                is LessonState.Loading -> {
                    handleLoadingState(this@RegisterLessonActivity)
                }

                is LessonState.Success -> {
                    Log.d("fetch Success", "${it.data}")
                    setLessonSiteList(it.data)

                    initViews()
                }

                is LessonState.Unauthorized -> showLogoutDialog()

                is LessonState.NotFound, LessonState.Forbidden -> {
                    Toast.makeText(this, "강의 사이트 정보를 가져오지 못했어요", Toast.LENGTH_SHORT).show()
                }

                is LessonState.Error -> {
                    Log.d("fetch Error", "${it.exception}")
                    Toast.makeText(this, "강의 사이트 정보를 가져오지 못했어요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setLessonSiteList(data: List<LessonSiteResponse>) = with(binding) {
        lessonSiteMap = data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
        lessonSiteList = data.map { it.siteName }.toMutableList()
        lessonSiteList.add(0, "강의 사이트를 선택해 주세요")
    }


}