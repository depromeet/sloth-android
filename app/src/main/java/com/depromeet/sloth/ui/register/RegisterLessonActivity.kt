package com.depromeet.sloth.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.databinding.ActivityRegisterLessonBinding
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterLessonActivity : BaseActivity<ActivityRegisterLessonBinding>() {

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewModel: RegisterViewModel by viewModels()

    lateinit var accessToken: String
    lateinit var refreshToken: String

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

    override fun getViewBinding(): ActivityRegisterLessonBinding =
        ActivityRegisterLessonBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLessonFirstFragment = RegisterLessonFirstFragment()
        registerLessonSecondFragment = RegisterLessonSecondFragment()
        registerLessonCheckFragment = RegisterLessonCheckFragment()

        accessToken = preferenceManager.getAccessToken()
        refreshToken = preferenceManager.getRefreshToken()

        mainScope {
            initLessonCategory()
            initLessonSite()
        }
    }

    override fun initViews() = with(binding) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_register_lesson, registerLessonFirstFragment)
            addToBackStack(null)
            commit()
        }

        tbRegisterLesson.setNavigationOnClickListener { onBackPressed() }
    }

    fun changeFragment(fragment: Fragment, args: Bundle? = null, backPressed: Boolean? = false) {
        if(args != null) {
            fragment.arguments = args
        }
        if(backPressed == true) {
            supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.slide_left_enter, R.anim.slide_left_exit)
                replace(R.id.fl_register_lesson, fragment)
                addToBackStack(null)
                commit()
            }
        }
        else {
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
        viewModel.fetchLessonCategoryList(accessToken = accessToken).let {
            when (it) {
                is LessonState.Success<List<LessonCategoryResponse>> -> {
                    Log.d("fetch Success", "${it.data}")
                    setLessonCategoryList(it.data)
                }
                is LessonState.Error -> {
                    Log.d("fetch Error", "${it.exception}")
                }

                is LessonState.Unauthorized -> {
                    val dlg = SlothDialog(this, DialogState.FORBIDDEN)
                    dlg.onItemClickListener =
                        object : SlothDialog.OnItemClickedListener {
                            override fun onItemClicked() {
                                preferenceManager.removeAuthToken()
                                startActivity(LoginActivity.newIntent(this@RegisterLessonActivity))
                            }
                        }
                    dlg.start()
                }
                is LessonState.NotFound -> {
                    Log.d("Error", "NotFound")
                }
                is LessonState.Forbidden -> {
                    Log.d("Error", "Forbidden")
                }
            }
        }
    }

    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) {
        lessonCategoryMap =
            data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>

        lessonCategoryList = data.map { it.categoryName }.toMutableList()
        lessonCategoryList.add(0, "강의 카테고리를 선택해 주세요.")
    }

    private suspend fun initLessonSite() {
        viewModel.fetchLessonSiteList(accessToken = accessToken).let {
            when (it) {
                is LessonState.Success -> {
                    Log.d("fetch Success", "${it.data}")
                    setLessonSiteList(it.data)

                    initViews()
                }
                is LessonState.Error -> {
                    Log.d("fetch Error", "${it.exception}")
                }

                is LessonState.Unauthorized -> {
                    val dlg = SlothDialog(this, DialogState.FORBIDDEN)
                    dlg.onItemClickListener =
                        object : SlothDialog.OnItemClickedListener {
                            override fun onItemClicked() {
                                preferenceManager.removeAuthToken()
                                startActivity(LoginActivity.newIntent(this@RegisterLessonActivity))
                            }
                        }
                    dlg.start()
                }
                is LessonState.NotFound -> {
                    Log.d("Error", "NotFound")
                }
                is LessonState.Forbidden -> {
                    Log.d("Error", "Forbidden")
                }
            }
        }
    }

    private fun setLessonSiteList(data: List<LessonSiteResponse>) {
        lessonSiteMap = data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>

        lessonSiteList = data.map { it.siteName }.toMutableList()
        lessonSiteList.add(0, "강의 사이트를 선택해 주세요.")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun unlockButton(button: AppCompatButton) {
        button.apply {
            isEnabled = true
            background = AppCompatResources.getDrawable(
                this@RegisterLessonActivity, R.drawable.bg_register_rounded_button_sloth
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun lockButton(button: AppCompatButton) {
        button.apply{
            isEnabled = false
            background = AppCompatResources.getDrawable(
                this@RegisterLessonActivity, R.drawable.bg_register_rounded_button_disabled
            )
        }
    }
}