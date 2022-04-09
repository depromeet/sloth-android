package com.depromeet.sloth.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.member.MemberUpdateInfoResponse
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.EventObserver
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import com.depromeet.sloth.util.LoadingDialogUtil
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class LessonDetailActivity : BaseActivity<ActivityLessonDetailBinding>() {
    private val viewModel: LessonDetailViewModel by viewModels()

    lateinit var lessonId: String
    lateinit var totalNumber: String
    lateinit var presentNumber: String
    lateinit var startDateInfo: String
    lateinit var endDateInfo: String
    lateinit var lesson: Lesson

    private var startDay: Long? = null
    private var isLessonStarted = true
    private val today = Date().time

    override fun getActivityBinding(): ActivityLessonDetailBinding =
        ActivityLessonDetailBinding.inflate(layoutInflater)

    companion object {
        fun newIntent(context: Context, lessonId: String) =
            Intent(context, LessonDetailActivity::class.java).apply {
                putExtra(LESSON_ID, lessonId)
            }

        private const val LESSON_ID = "lessonId"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.apply {
            lessonId = getStringExtra(LESSON_ID).toString()
        }

        viewModel.apply {
            lessonDeleteState.observe(this@LessonDetailActivity) { lessonDeleteState ->
                Log.d("LessonDetailActivity", "lessonDeleteState onChange 호출")
                when (lessonDeleteState) {
                    is LessonDeleteState.Loading -> handleLoadingState(this@LessonDetailActivity)

                    is LessonDeleteState.Success<LessonDeleteResponse> -> handleSuccessState(
                        lessonDeleteState.data)

                    is LessonDeleteState.Unauthorized -> showLogoutDialog()

                    is LessonDeleteState.NoContent, LessonDeleteState.Forbidden -> {
                        Toast.makeText(this@LessonDetailActivity,
                            "강의를 삭제하지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()
                    }

                    is LessonDeleteState.Error -> {
                        Log.d("fetch Error", "${lessonDeleteState.exception}")
                        Toast.makeText(this@LessonDetailActivity,
                            "강의를 삭제하지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()
                    }

                    is LessonDeleteState.Finish -> {
                        Toast.makeText(this@LessonDetailActivity,
                            "강의가 삭제 되었어요",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                hideProgress()
            }
        }

        initViews()
    }

    override fun onStart() {
        super.onStart()

        mainScope {
            viewModel.fetchLessonDetail(lessonId = lessonId).let {
                when (it) {
                    is LessonDetailState.Loading -> {
                        handleLoadingState(this)
                    }

                    is LessonDetailState.Success<LessonDetailResponse> -> {
                        Log.d("fetch Success", "${it.data}")

                        setLessonInfo(it.data)
                    }

                    is LessonDetailState.Unauthorized -> {
                        showLogoutDialog()
                    }

                    is LessonDetailState.NotFound, LessonDetailState.Forbidden -> {
                        Toast.makeText(this, "강의 상세 정보를 가져오지 못했어요", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is LessonDetailState.Error -> {
                        Log.d("Error", "${it.exception}")
                    }
                }
            }
        }
    }

    private fun <T> handleSuccessState(data: T) {
        if (data is LessonDeleteResponse) {
            Toast.makeText(this@LessonDetailActivity, "강의가 삭제 되었어요", Toast.LENGTH_SHORT).show()
            finish()
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

    override fun initViews() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }

        tvDetailUpdateLesson.setOnClickListener {
            startActivity(
                UpdateLessonActivity.newIntent(this@LessonDetailActivity, lessonId, lesson)
            )
        }

        btnDetailDeleteLesson.setOnClickListener {
            showLessonDeleteDialog()
        }
    }

    private fun showLessonDeleteDialog() {
        val dlg = SlothDialog(this@LessonDetailActivity, DialogState.DELETE_LESSON)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                deleteLesson(lessonId)
                finish()
            }
        }
        dlg.start()
    }

    private fun deleteLesson(lessonId: String) = mainScope {
        viewModel.deleteLesson(lessonId)
    }

    @SuppressLint("SetTextI18n")
    private fun setLessonInfo(data: LessonDetailResponse) = with(binding) {
        lesson = Lesson(
            alertDays = data.alertDays,
            categoryName = data.categoryName,
            endDate = data.endDate.toString(),
            lessonName = data.lessonName,
            message = data.message,
            price = data.price,
            siteName = data.siteName,
            startDate = data.startDate.toString(),
            totalNumber = data.totalNumber,
        )
        // 현재 진행율
        pbDetailCurrentLessonProgress.labelText = "${data.currentProgressRate}%"
        pbDetailCurrentLessonProgress.progress = data.currentProgressRate.toFloat()

        // 목표 진행율
        pbDetailGoalLessonProgress.labelText =
            if (data.goalProgressRate > 100) {
                getString(R.string.one_hundred_percent)
            } else {
                "${data.goalProgressRate}%"
            }

        pbDetailGoalLessonProgress.progress =
            if (data.goalProgressRate > 100) {
                100F
            } else {
                data.goalProgressRate.toFloat()
            }

        // 강의 요약
        if (data.currentProgressRate >= data.goalProgressRate) {
            tvDetailLessonSummary.setText(R.string.mission_success)
            ivDetailSloth.setImageResource(R.drawable.ic_detail_sloth_steadily_listen)
        } else {
            tvDetailLessonSummary.setText(R.string.mission_fail)
            ivDetailSloth.setImageResource(R.drawable.ic_detail_sloth_fail_goal)
        }

        // 현재 내가 날린 돈
        tvDetailLessonLoseMoneyInfo.text = changeDecimalFormat(data.wastePrice)

        // 남은 날짜
        tvDetailLessonRemainDay.text =
            if (data.remainDay < 0) {
                "D+${(data.remainDay) * -1}"
            } else {
                if (data.remainDay == 0) {
                    getString(R.string.d_day)
                } else {
                    "D-${(data.remainDay)}"
                }
            }

        // 마감 임박
        if (data.remainDay in 0..10) {
            tvDetailLessonWarning.visibility = View.VISIBLE
            tvDetailLessonWarning.text = getString(R.string.lesson_warning)
            tvDetailLessonRemainDay.setTextColor(
                ContextCompat.getColor(
                    this@LessonDetailActivity,
                    R.color.error
                )
            )
        }

        // 마감
        if (data.remainDay < 0) {
            tvDetailLessonWarning.visibility = View.VISIBLE
            tvDetailLessonWarning.background = AppCompatResources.getDrawable(
                this@LessonDetailActivity,
                R.drawable.bg_rounded_chip_black
            )
            tvDetailLessonWarning.text = getString(R.string.lesson_close)
        }

        // 강의 카테고리
        tvDetailLessonCategory.text = data.categoryName

        // 강의 사이트
        tvDetailLessonSite.text = data.siteName

        totalNumber = data.totalNumber.toString()

        // 내가 들은 강의
        presentNumber = data.presentNumber.toString()
        tvDetailLessonPresentNumberInfo.text =
            " ${totalNumber}개 중 ${presentNumber}개"

        // 강의 개수
        tvDetailLessonCountInfo.text = totalNumber

        // 강의 시작 날짜
        startDateInfo = changeDateFormatToDot(data.startDate)
        startDay = stringToDate(changeDateFormatToDash(data.startDate))?.time
        isLessonStarted = startDay!! <= today

        // 강의 종료 날짜
        endDateInfo = changeDateFormatToDot(data.endDate)

        // 목표 완강일
        tvDetailLessonEndDateInfo.text = " $endDateInfo"

        // 수강 기간
        tvDetailLessonPeriodInfo.text = " $startDateInfo - $endDateInfo"
        tvDetailLessonName.text = data.lessonName

        // 강의 금액
        tvDetailLessonPriceInfo.text = changeDecimalFormat(data.price)

        // 각오 한 마디
        tvDetailLessonMessageInfo.text = data.message
    }
}