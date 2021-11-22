package com.depromeet.sloth.ui.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
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
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.ui.base.BaseActivity
import java.text.DecimalFormat

class LessonDetailActivity : BaseActivity<LessonDetailViewModel, ActivityLessonDetailBinding>() {

    override val viewModel: LessonDetailViewModel = LessonDetailViewModel()

    override fun getViewBinding(): ActivityLessonDetailBinding =
        ActivityLessonDetailBinding.inflate(layoutInflater)


    private val pm = PreferenceManager(this)

    lateinit var accessToken: String

    lateinit var lessonId: String

    lateinit var totalNumber: String

    lateinit var presentNumber: String

    lateinit var startDate: ArrayList<String>

    lateinit var endDate: ArrayList<String>

    lateinit var startDateInfo: String

    lateinit var endDateInfo: String

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

        /*intent.apply {
            lessonId = getStringExtra(LESSON_ID).toString()
            lessonModel = getParcelableExtra(LESSON_MODEL)
        }*/

        /*test*/
        lessonId = "6"

        mainScope {
            viewModel.fetchLessonDetailInfo(accessToken = accessToken, lessonId = lessonId).let {
                when (it) {
                    is LessonDetailState.Success -> {
                        Log.d("fetch Success", "${it.data}")

                        initLessonInfo(it.data)
                    }

                    is LessonDetailState.Error -> {
                        Log.d("fetch Error", "${it.exception}")
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }


        tvDetailUpdateLesson.setOnClickListener {
            //UpdateLessonActivity.newIntent(this, lessonId, lessonModel)
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

            startDate = data.startDate
            startDateInfo = changeDateFormat(startDate)

            endDate = data.endDate
            endDateInfo = changeDateFormat(endDate)

            /*목표 완강일*/
            tvDetailLessonEndDateInfo.text = " $endDateInfo"

            /*완강 목표일*/
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

        return "${yearOfDate}.${monthOfDate}.$dayOfDate"
    }

    private fun changeDate(data: String): String {
        var tmp = data
        if (tmp.length == 1) {
            tmp = "0$tmp"
        }
        return tmp
    }
}