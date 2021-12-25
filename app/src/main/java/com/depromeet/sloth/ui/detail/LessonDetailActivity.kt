package com.depromeet.sloth.ui.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.depromeet.sloth.R
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.detail.LessonDetailState
import com.depromeet.sloth.data.network.register.RegisterLessonRequest
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import java.text.DecimalFormat

class LessonDetailActivity : BaseActivity<LessonDetailViewModel, ActivityLessonDetailBinding>() {

    override val viewModel: LessonDetailViewModel = LessonDetailViewModel()

    override fun getViewBinding(): ActivityLessonDetailBinding =
        ActivityLessonDetailBinding.inflate(layoutInflater)


    private val pm = PreferenceManager(this)

    lateinit var accessToken: String

    lateinit var refreshToken: String

    lateinit var lessonId: String

    lateinit var totalNumber: String

    lateinit var presentNumber: String

    lateinit var startDateInfo: String

    lateinit var endDateInfo: String

    lateinit var lesson: RegisterLessonRequest

    lateinit var categoryArray: Array<String>

    lateinit var siteArray: Array<String>

    companion object {
        fun newIntent(activity: Activity, lessonId: String) =
            Intent(activity, LessonDetailActivity::class.java).apply {
                putExtra(LESSON_ID, lessonId)
            }

        private const val LESSON_ID = "lessonId"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        accessToken = pm.getAccessToken().toString()

        refreshToken = pm.getRefreshToken().toString()

        categoryArray = resources.getStringArray(R.array.category_array)

        siteArray = resources.getStringArray(R.array.site_array)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        intent.apply {
            lessonId = getStringExtra(LESSON_ID).toString()
        }

        /*test*/
        //lessonId = "6"

        mainScope {
            viewModel.fetchLessonDetailInfo(accessToken = accessToken, lessonId = lessonId).let {
                when (it) {
                    is LessonDetailState.Success -> {
                        Log.d("fetch Success", "${it.data}")

                        initLessonInfo(it.data)
                    }

                    is LessonDetailState.Unauthorized -> {
                        viewModel.fetchLessonDetailInfo(accessToken = refreshToken, lessonId = lessonId).let { lessonDetailResponse ->
                            when (lessonDetailResponse) {
                                is LessonDetailState.Success -> {
                                    Log.d("fetch Success", "${lessonDetailResponse.data}")

                                    initLessonInfo(lessonDetailResponse.data)
                                }

                                is LessonDetailState.Error -> {
                                    Log.d("fetch Error", "${lessonDetailResponse.exception}")
                                }
                                else -> Unit
                            }
                        }
                    }

                    is LessonDetailState.Error -> {
                        Log.d("fetch Error", "${it.exception}")
                    }

                    else ->  Unit
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }


        tvDetailUpdateLesson.setOnClickListener {
            startActivity(UpdateLessonActivity.newIntent(this@LessonDetailActivity, lessonId, lesson))
        }

        btnDetailDeleteLesson.setOnClickListener {
            val dlg = LessonDeleteDialog(this@LessonDetailActivity)
            dlg.listener = object: LessonDeleteDialog.LessonDeleteDialogClickedListener {
                override fun onDeleteClicked() {
                    deleteLesson(accessToken, lessonId)
                    Toast.makeText(this@LessonDetailActivity, "강의가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    finish() /*화면 종료*/
                }
            }
            dlg.start()
        }

    }

    private fun deleteLesson(accessToken: String, lessonId: String) {
        mainScope {
            viewModel.deleteLesson(accessToken = accessToken, lessonId = lessonId).let {
                when(it) {
                    is LessonDetailState.Success<*> -> {
                        Log.d("Delete Success", "${it.data}")
                    }

                    is LessonDetailState.Unauthorized -> {
                        viewModel.deleteLesson(accessToken = refreshToken, lessonId = lessonId).let { deleteLessonResponse ->
                            when (deleteLessonResponse) {
                                is LessonDetailState.Success -> {
                                    Log.d("Delete Success", "${deleteLessonResponse.data}")
                                }

                                is LessonDetailState.Error -> {
                                    Log.d("Delete Error", "${deleteLessonResponse.exception}")
                                }
                                else -> Unit
                            }
                        }
                    }

                    is LessonDetailState.Error -> {
                        Log.d("Delete Error", "${it.exception}")
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initLessonInfo(data: LessonDetailResponse) {

        binding.apply {

            lesson = RegisterLessonRequest(
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

            /*현재 진행율 */
            pbDetailCurrentLessonProgress.labelText = "${data.currentProgressRate}%"
            pbDetailCurrentLessonProgress.progress = data.currentProgressRate.toFloat()

            /*목표 진행율 */
            pbDetailGoalLessonProgress.labelText = "${data.goalProgressRate}%"
            pbDetailGoalLessonProgress.progress = data.goalProgressRate.toFloat()

            /*강의 요약*/
            if(data.currentProgressRate >= data.goalProgressRate) {
                tvDetailLessonSummary.setText(R.string.mission_success)
            }
            else {
                tvDetailLessonSummary.setText(R.string.mission_fail)
            }

            /*현재 내가 날린 돈*/
            tvDetailLessonLoseMoneyInfo.text = changeDecimalFormat(data.wastePrice)

            /*남은 날짜*/
            tvDetailLessonRemainDay.text = "D-${data.remainDay}"

            /*마감 임박*/
            if (data.remainDay <= 10) {
                tvDetailLessonWarning.visibility = View.VISIBLE
                tvDetailLessonRemainDay.setTextColor(ContextCompat.getColor(this@LessonDetailActivity, R.color.error))
            }

            /*강의 카테고리*/
            tvDetailLessonCategory.text = data.categoryName

            /*강의 사이트*/
            tvDetailLessonSite.text = data.siteName

            totalNumber = data.totalNumber.toString()

            /*내가 들은 강의*/
            presentNumber = data.presentNumber.toString()
            tvDetailLessonPresentNumberInfo.text =
                " ${totalNumber}개 중 ${presentNumber}개"

            /*강의 개수*/
            tvDetailLessonCountInfo.text = totalNumber

            /*강의 시작 날짜*/
            startDateInfo = changeDateFormat(data.startDate)

            /*강의 종료 날짜*/
            endDateInfo = changeDateFormat(data.endDate)

            /*목표 완강일*/
            tvDetailLessonEndDateInfo.text = " $endDateInfo"

            /*수강 기간*/
            tvDetailLessonPeriodInfo.text = " $startDateInfo - $endDateInfo"
            tvDetailLessonName.text = data.lessonName

            /*강의 금액*/
            tvDetailLessonPriceInfo.text = changeDecimalFormat(data.price)

            /*각오 한 마디*/
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