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
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.model.LessonCategory
import com.depromeet.sloth.data.model.LessonSite
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class LessonDetailActivity : BaseActivity<ActivityLessonDetailBinding>() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewModel: LessonDetailViewModel by viewModels()

    lateinit var accessToken: String
    lateinit var refreshToken: String
    lateinit var lessonId: String
    lateinit var lessonPrice: Number
    lateinit var totalNumber: String
    lateinit var presentNumber: String
    lateinit var startDateInfo: String
    lateinit var endDateInfo: String
    lateinit var lesson: LessonRegisterRequest
    lateinit var lessonDetailInfo: LessonDetailResponse

    lateinit var lessonCategoryMap: HashMap<Int, String>
    private var lessonCategoryList = mutableListOf<String>()

    lateinit var lessonSiteMap: HashMap<Int, String>
    private var lessonSiteList = mutableListOf<String>()

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
        private const val LESSON_PRICE = "lessonPrice"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accessToken = preferenceManager.getAccessToken()
        refreshToken = preferenceManager.getRefreshToken()

        initViews()

        intent.apply {
            lessonId = getStringExtra(LESSON_ID).toString()
        }
    }

    override fun onStart() {
        super.onStart()

        mainScope {
            viewModel.fetchLessonDetail(accessToken = accessToken, lessonId = lessonId).let {
                when (it) {
                    is LessonState.Success<LessonDetailResponse> -> {
                        Log.d("fetch Success", "${it.data}")
                        lessonDetailInfo = it.data

                        mainScope {
                            initLessonCategoryList()
                            initLessonSiteList()
                        }
                    }

                    is LessonState.Unauthorized -> {
                        Log.d("Unauthorized", "${it.exception}")
                        val dlg = SlothDialog(this, DialogState.FORBIDDEN)
                        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                            override fun onItemClicked() {
                                preferenceManager.removeAuthToken()
                                startActivity(LoginActivity.newIntent(this@LessonDetailActivity))
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

                    is LessonState.Error -> {
                        Log.d("Error", "${it.exception}")
                    }
                }
            }
        }
    }

    private suspend fun initLessonCategoryList() {
        viewModel.fetchLessonCategoryList(accessToken = accessToken).let {
            when (it) {
//                is LessonState.Success<List<LessonCategory>> -> {
                is LessonState.Success<List<LessonCategoryResponse>> -> {
                    Log.d("fetch Success", "${it.data}")
                    setLessonCategoryList(it.data)
                }
                is LessonState.Unauthorized -> {
                    val dlg = SlothDialog(this@LessonDetailActivity, DialogState.FORBIDDEN)
                    dlg.onItemClickListener =
                        object : SlothDialog.OnItemClickedListener {
                            override fun onItemClicked() {
                                preferenceManager.removeAuthToken()
                                startActivity(LoginActivity.newIntent(this@LessonDetailActivity))
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
                is LessonState.Error -> {
                    Log.d("fetch Error", "${it.exception}")
                }
            }
        }
    }

    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) {
        lessonCategoryMap =
            data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>

        lessonCategoryList = data.map { it.categoryName }.toMutableList()
        lessonCategoryList.add(0, "인강 카테고리를 선택해 주세요")
    }

//    private fun setLessonCategoryList(data: List<LessonCategory>) {
//        lessonCategoryMap =
//            data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
//
//        lessonCategoryList = data.map { it.categoryName }.toMutableList()
//        lessonCategoryList.add(0, "인강 카테고리를 선택해 주세요")
//    }

    private suspend fun initLessonSiteList() {
        viewModel.fetchLessonSiteList(accessToken = accessToken).let {
            when (it) {
                is LessonState.Success -> {
                    Log.d("fetch Success", "${it.data}")
                    setLessonSiteList(it.data)
                    initLessonInfo(lessonDetailInfo)
                }

                is LessonState.Unauthorized -> {
                    val dlg = SlothDialog(this@LessonDetailActivity, DialogState.FORBIDDEN)
                    dlg.onItemClickListener =
                        object : SlothDialog.OnItemClickedListener {
                            override fun onItemClicked() {
                                preferenceManager.removeAuthToken()
                                startActivity(LoginActivity.newIntent(this@LessonDetailActivity))
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
                is LessonState.Error -> {
                    Log.d("fetch Error", "${it.exception}")
                }
            }
        }
    }

    private fun setLessonSiteList(data: List<LessonSiteResponse>) {
        lessonSiteMap = data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>

        lessonSiteList = data.map { it.siteName }.toMutableList()
        lessonSiteList.add(0, "강의 사이트를 선택해 주세요")
    }

//    private fun setLessonSiteList(data: List<LessonSite>) {
//        lessonSiteMap = data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
//
//        lessonSiteList = data.map { it.siteName }.toMutableList()
//        lessonSiteList.add(0, "강의 사이트를 선택해 주세요")
//    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }

        tvDetailUpdateLesson.setOnClickListener {
            startActivity(
                UpdateLessonActivity.newIntent(this@LessonDetailActivity, lessonId, lesson)
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
                        Toast.makeText(this, "강의가 삭제되었어요", Toast.LENGTH_SHORT).show()
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
            lesson = LessonRegisterRequest (
                alertDays = data.alertDays,
                categoryId =
                lessonCategoryMap.filterValues
                { it == data.categoryName }.keys.first(),
                // == lessonCategoryMap.entries.find {it.value == data.categoryName}?.key,
                endDate = data.endDate.toString(),
                lessonName = data.lessonName,
                message = data.message,
                price = data.price,
                siteId = lessonSiteMap.filterValues
                { it == data.siteName }.keys.first(),
                // == lessonSiteMap.entries.find {it.value == data.siteName}?.key,
                startDate = data.startDate.toString(),
                totalNumber = data.totalNumber,
            )
            Log.d("LessonRegisterRequest", "$lesson")

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
}