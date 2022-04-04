package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_GOAL_DATE
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_MESSAGE
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_PRICE
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_PUSH_NOTI_CYCLE
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_START_DATE
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class RegisterLessonCheckFragment : BaseFragment<FragmentRegisterLessonCheckBinding>() {

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewModel: RegisterViewModel by activityViewModels()

    lateinit var accessToken: String
    lateinit var refreshToken: String

    lateinit var lessonName: String
    lateinit var lessonCount: Number
    lateinit var lessonCategoryName: String
    lateinit var lessonSiteName: String
    lateinit var lessonPrice: Number
    lateinit var lessonPushNotiCycle: String
    lateinit var lessonStartDate: String
    lateinit var lessonGoalDate: String
    lateinit var lessonMessage: String

    lateinit var categoryMap: HashMap<Int, String>
    lateinit var siteMap: HashMap<Int, String>

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentRegisterLessonCheckBinding {
        return FragmentRegisterLessonCheckBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as RegisterLessonActivity).apply {
            categoryMap = lessonCategoryMap
            siteMap = lessonSiteMap
        }

        accessToken = preferenceManager.getAccessToken()
        refreshToken = preferenceManager.getRefreshToken()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun initViews() = with(binding) {
        arguments?.apply {
            lessonName = getString(RegisterLessonFirstFragment.LESSON_NAME).toString()
            lessonCount = getInt(RegisterLessonFirstFragment.LESSON_COUNT)
            lessonCategoryName =
                getString(RegisterLessonFirstFragment.LESSON_CATEGORY_NAME).toString()
            lessonSiteName = getString(RegisterLessonFirstFragment.LESSON_SITE_NAME).toString()
            lessonPrice = getInt(LESSON_PRICE)
            lessonPushNotiCycle = getString(LESSON_PUSH_NOTI_CYCLE).toString()
            lessonStartDate = getString(LESSON_START_DATE).toString()
            lessonGoalDate = getString(LESSON_GOAL_DATE).toString()
            lessonMessage = getString(LESSON_MESSAGE).toString()
        }

        tvRegisterLessonName.text = lessonName
        tvRegisterLessonCountInfo.text = getString(R.string.input_lesson_count, lessonCount)
        tvRegisterLessonCategoryInfo.text = lessonCategoryName
        tvRegisterLessonSiteInfo.text = lessonSiteName
        tvRegisterLessonPriceInfo.text = changeDecimalFormat(lessonPrice as Int)
        //tvRegisterLessonPushNotiCycle.text = lessonPushNotiCycle.toString()
        tvRegisterLessonStartDateInfo.text = changeDateFormat(lessonStartDate)
        tvRegisterLessonGoalDateInfo.text = changeDateFormat(lessonGoalDate)

        btnRegisterLessonUpdate.setOnClickListener {
            (activity as RegisterLessonActivity).onBackPressed()
        }

        btnRegisterLessonAccept.setOnClickListener {
            mainScope {
                viewModel.registerLesson(accessToken,
                    LessonRegisterRequest(
                        alertDays = lessonPushNotiCycle,
                        categoryId =
                        categoryMap.filterValues
                        { it == lessonCategoryName }.keys.first(),
                        endDate = lessonGoalDate,
                        lessonName = lessonName,
                        message = lessonMessage,
                        price = lessonPrice as Int,
                        siteId =
                        siteMap.filterValues
                        { it == lessonSiteName }.keys.first(),
                        startDate = lessonStartDate,
                        totalNumber = lessonCount as Int
                    )
                ).let {
                    when (it) {
                        is LessonState.Success -> {
                            Log.d("Register Success", "${it.data}")
                            Toast.makeText(requireContext(), "강의가 등록되었어요", Toast.LENGTH_SHORT).show()
                            (activity as RegisterLessonActivity).finish()
                        }

                        is LessonState.Unauthorized -> {
                            val dlg = SlothDialog(requireContext(), DialogState.FORBIDDEN)
                            dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                                override fun onItemClicked() {
                                    preferenceManager.removeAuthToken()
                                    startActivity(LoginActivity.newIntent(requireActivity()))
                                }
                            }
                            dlg.start()
                        }

                        is LessonState.Error -> {
                            Log.d("Register Error", "${it.exception}")
                            Toast.makeText(requireContext(), "강의 등록을 실패했어요", Toast.LENGTH_SHORT).show()
                        }

                        is LessonState.NotFound -> {
                            Log.d("Update Error", "NotFound")
                        }
                        is LessonState.Forbidden -> {
                            Log.d("Update Error", "Forbidden")
                        }
                    }
                }
            }
        }
    }

    private fun changeDecimalFormat(data: Int): String {
        val df = DecimalFormat("#,###")
        val changedPriceFormat = df.format(data)

        return "${changedPriceFormat}원"
    }

    private fun changeDateFormat(date: String): String {
        val dateArr = date.split("-")

        val yearOfDate = dateArr[0]
        val monthOfDate = dateArr[1]
        val dayOfDate = dateArr[2]

        return "${yearOfDate}.${monthOfDate}.$dayOfDate"
    }
}