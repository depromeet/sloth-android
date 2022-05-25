package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.extensions.changeDateStringToArrayList
import com.depromeet.sloth.extensions.handleLoadingState
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.EventObserver
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_CATEGORY_ID
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_CATEGORY_NAME
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_TOTAL_NUMBER
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_NAME
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_SITE_ID
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_SITE_NAME
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_END_DATE
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_MESSAGE
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_PRICE
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_ALERT_DAYS
import com.depromeet.sloth.ui.register.RegisterLessonSecondFragment.Companion.LESSON_START_DATE
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RegisterLessonCheckFragment : BaseFragment<FragmentRegisterLessonCheckBinding>(R.layout.fragment_register_lesson_check) {

    private val viewModel: RegisterLessonViewModel by activityViewModels()

    lateinit var lessonName: String
    lateinit var lessonTotalNumber: Number
    lateinit var lessonCategoryName: String
    lateinit var lessonCategoryId: Number
    lateinit var lessonSiteName: String
    lateinit var lessonSiteId: Number
    lateinit var lessonPrice: Number
    lateinit var lessonAlertDays: String
    lateinit var lessonStartDate: String
    lateinit var lessonEndDate: String
    lateinit var lessonMessage: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag("Third").d("${this.hashCode()}")

        Timber.d("$arguments")
        arguments?.apply {
            lessonName = getString(LESSON_NAME).toString()
            lessonTotalNumber = getInt(LESSON_TOTAL_NUMBER)
            lessonCategoryName =
                getString(LESSON_CATEGORY_NAME).toString()
            lessonCategoryId = getInt(LESSON_CATEGORY_ID)
            lessonSiteName = getString(LESSON_SITE_NAME).toString()
            lessonSiteId = getInt(LESSON_SITE_ID)
            lessonPrice = getInt(LESSON_PRICE)
            lessonAlertDays = getString(LESSON_ALERT_DAYS).toString()
            lessonStartDate = getString(LESSON_START_DATE).toString()
            lessonEndDate = getString(LESSON_END_DATE).toString()
            lessonMessage = getString(LESSON_MESSAGE).toString()
        }

        viewModel.apply {
            lessonRegisterState.observe(viewLifecycleOwner,
                EventObserver { lessonRegisterResponse ->
                    when (lessonRegisterResponse) {
                        is LessonState.Loading -> handleLoadingState(requireContext())

                        is LessonState.Success -> {
                            Timber.tag("Register Success").d("${lessonRegisterResponse.data}")
                            showToast("강의가 등록되었어요")
                            (activity as RegisterLessonActivity).finish()
                        }

                        is LessonState.Unauthorized -> {
                            showLogoutDialog(
                                requireContext(),
                                requireActivity()
                            ) { viewModel.removeAuthToken() }
                        }

                        is LessonState.Error -> {
                            Timber.tag("Register Error").d(lessonRegisterResponse.throwable)
                            showToast("강의 등록을 실패했어요")
                        }
                    }
                    hideProgress()
                })

            lessonRegister.observe(viewLifecycleOwner) { lessonRegister ->
                binding.lessonRegister = lessonRegister
            }
        }

        initViews()
    }

    override fun initViews() = with(binding) {
        viewModel.setLessonRegisterInfo(
            Lesson(
                alertDays = lessonAlertDays,
                categoryName = lessonCategoryName,
                endDate = changeDateStringToArrayList(lessonEndDate),
                lessonName = lessonName,
                message = lessonMessage,
                price = lessonPrice as Int,
                siteName = lessonSiteName,
                startDate = changeDateStringToArrayList(lessonStartDate),
                totalNumber = lessonTotalNumber as Int,
            )
        )

        btnRegisterLessonUpdate.setOnClickListener {
            (activity as RegisterLessonActivity).navController.navigateUp()
        }

        btnRegisterLessonAccept.setOnClickListener {
            viewModel.registerLesson(
                LessonRegisterRequest(
                    lessonAlertDays,
                    lessonCategoryId as Int,
                    lessonEndDate,
                    lessonName,
                    lessonMessage,
                    lessonPrice as Int,
                    lessonSiteId as Int,
                    lessonStartDate,
                    lessonTotalNumber as Int
                )
            )
        }
    }
}