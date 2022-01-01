package com.depromeet.sloth.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import java.text.DecimalFormat

class LessonDetailActivity : BaseActivity<LessonDetailViewModel, ActivityLessonDetailBinding>() {
    private val preferenceManager = PreferenceManager(this)
    lateinit var accessToken: String
    lateinit var refreshToken: String
    lateinit var lessonId: String
    lateinit var lessonPrice: Number
    lateinit var totalNumber: String
    lateinit var presentNumber: String
    lateinit var startDateInfo: String
    lateinit var endDateInfo: String
    lateinit var lesson: LessonRegisterRequest
    lateinit var categoryArray: Array<String>
    lateinit var siteArray: Array<String>

    override val viewModel: LessonDetailViewModel = LessonDetailViewModel(preferenceManager)

    override fun getViewBinding(): ActivityLessonDetailBinding =
        ActivityLessonDetailBinding.inflate(layoutInflater)

    companion object {
        fun newIntent(context: Context, lessonId: String, lessonPrice: Int) =
            Intent(context, LessonDetailActivity::class.java).apply {
                putExtra(LESSON_ID, lessonId)
                putExtra(LESSON_PRICE, lessonPrice)
            }

        private const val LESSON_ID = "lessonId"
        private const val LESSON_PRICE = "lessonPrice"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        accessToken = preferenceManager.getAccessToken()

        refreshToken = preferenceManager.getRefreshToken()

        categoryArray = resources.getStringArray(R.array.category_array)

        siteArray = resources.getStringArray(R.array.site_array)

    }

    override fun onResume() {
        super.onResume()

        intent.apply {
            lessonId = getStringExtra(LESSON_ID).toString()
            lessonPrice = getIntExtra(LESSON_PRICE, 0)
        }

        // test
        //lessonId = "6"

        mainScope {
            viewModel.fetchLessonDetail(accessToken = accessToken, lessonId = lessonId).let {
                when (it) {
                    is LessonState.Success<LessonDetailResponse> -> {
                        Log.d("fetch Success", "${it.data}")

                        initLessonInfo(it.data)
                    }

                    is LessonState.Unauthorized -> {
                        viewModel.fetchLessonDetail(accessToken = refreshToken, lessonId = lessonId)
                            .let { lessonDetailResponse ->
                                when (lessonDetailResponse) {
                                    is LessonState.Success -> {
                                        Log.d("fetch Success", "${lessonDetailResponse.data}")

                                        initLessonInfo(lessonDetailResponse.data)
                                    }

                                    is LessonState.Unauthorized -> {
                                        val dlg = SlothDialog(
                                            this@LessonDetailActivity,
                                            DialogState.FORBIDDEN
                                        )
                                        dlg.onItemClickListener =
                                            object : SlothDialog.OnItemClickedListener {
                                                override fun onItemClicked() {
                                                    preferenceManager.removeAuthToken()
                                                    startActivity(LoginActivity.newIntent(this@LessonDetailActivity))
                                                }
                                            }
                                        dlg.start()
                                    }

                                    is LessonState.Forbidden -> {
                                        val dlg = SlothDialog(this, DialogState.FORBIDDEN)
                                        dlg.onItemClickListener =
                                            object : SlothDialog.OnItemClickedListener {
                                                override fun onItemClicked() {
                                                    preferenceManager.removeAuthToken()
                                                    startActivity(LoginActivity.newIntent(this@LessonDetailActivity))
                                                }
                                            }
                                        dlg.start()
                                    }


                                    is LessonState.Error -> {
                                        Log.d("fetch Error", "${lessonDetailResponse.exception}")
                                    }
                                    else -> Unit
                                }
                            }
                    }

                    is LessonState.Error -> {
                        Log.d("fetch Error", "${it.exception}")
                    }

                    else -> Unit
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }

        tvDetailUpdateLesson.setOnClickListener {
            startActivity(
                UpdateLessonActivity.newIntent(
                    this@LessonDetailActivity,
                    lessonId,
                    lesson
                )
            )
        }

        btnDetailDeleteLesson.setOnClickListener {
            val dlg = SlothDialog(this@LessonDetailActivity, DialogState.DELETE_LESSON)
            dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                override fun onItemClicked() {
                    deleteLesson(accessToken, lessonId)
                }
            }
            dlg.start()
        }
    }

    private fun deleteLesson(accessToken: String, lessonId: String) {
        mainScope {
            viewModel.deleteLesson(accessToken = accessToken, lessonId = lessonId).let {
                when (it) {
                    is LessonState.Success<*> -> {
                        Log.d("Success", "${it.data}")
                        Toast.makeText(this@LessonDetailActivity, "강의가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is LessonState.Unauthorized -> {
                        Log.d("Error", "${it.exception}")
                    }

                    is LessonState.NotFound -> {
                        Log.d("Error", "NotFound")
                    }
                    is LessonState.Forbidden -> {
                        Log.d("Error", "Forbidden")
                    }
                    is LessonState.Error -> {
                        Log.d("Error", "${it.exception}")
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initLessonInfo(data: LessonDetailResponse) {

        binding.apply {

            lesson = LessonRegisterRequest(
                alertDays = data.alertDays,
                categoryId = categoryArray.indexOf(data.categoryName),
                endDate = data.endDate.toString(),
                lessonName = data.lessonName,
                message = data.message,
                price = data.price,
                siteId = siteArray.indexOf(data.siteName),
                startDate = data.startDate.toString(),
                totalNumber = data.totalNumber,
            )

            // 현재 진행율
            pbDetailCurrentLessonProgress.labelText = "${data.currentProgressRate}%"
            pbDetailCurrentLessonProgress.progress = data.currentProgressRate.toFloat()

            // 목표 진행율
            pbDetailGoalLessonProgress.labelText =
                if (data.goalProgressRate > 100) {
                    "100%"
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
            } else {
                tvDetailLessonSummary.setText(R.string.mission_fail)
            }

            // 현재 내가 날린 돈
            tvDetailLessonLoseMoneyInfo.text =
                if (data.wastePrice > lessonPrice as Int) {
                    changeDecimalFormat(lessonPrice as Int)
                } else {
                    changeDecimalFormat(data.wastePrice)
                }

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
            startDateInfo = changeDateFormat(data.startDate)

            // 강의 종료 날짜
            endDateInfo = changeDateFormat(data.endDate)

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

    private fun changeDecimalFormat(data: Int): String {
        val df = DecimalFormat("#,###")
        val changedPriceFormat = df.format(data)

        return "${changedPriceFormat}원"
    }

    private fun changeDateFormat(date: ArrayList<String>): String {
        val yearOfDate = date[0]
        val monthOfDate = changeDate(date[1])
        val dayOfDate = changeDate(date[2])

        return "${yearOfDate}. ${monthOfDate}. $dayOfDate"
    }

    private fun changeDate(data: String): String {
        var tmp = data
        if (tmp.length == 1) {
            tmp = "0$tmp"
        }
        return tmp
    }
}