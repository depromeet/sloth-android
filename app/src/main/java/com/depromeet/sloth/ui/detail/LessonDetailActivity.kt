package com.depromeet.sloth.ui.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.depromeet.sloth.data.db.PreferenceManager
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

    lateinit var price: Number


    companion object {
        fun newIntent(activity: Activity, lessonId: String) =
            Intent(activity, LessonDetailActivity::class.java).apply {
                putExtra(LESSON_ID, lessonId)
            }

        private const val LESSON_ID = "lessonId"

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }

        accessToken = pm.getAccessToken().toString()

        /*intent.apply {
            lessonId = getStringExtra(LESSON_ID).toString()
            lessonModel = getParcelableExtra(LESSON_MODEL)
        }*/

        /*test*/
        lessonId = "80"

        mainScope {
            viewModel.fetchLessonDetailInfo(accessToken = accessToken, lessonId = lessonId).let {
                when (it) {
                    is LessonDetailState.Success -> {
                        Log.d("fetch Success", "${it.data}")
                        binding.apply {

                            tvDetailLessonCategory.text = it.data.categoryName
                            pbDetailCurrentLessonProgress.progress = it.data.currentProgressRate

                            startDate = it.data.startDate
                            startDateInfo = changeDateFormat(startDate)

                            endDate = it.data.endDate
                            endDateInfo = changeDateFormat(endDate)

                            tvDetailLessonPeriodInfo.text = "$startDateInfo - $endDateInfo"
                            pbDetailGoalLessonProgress.progress = it.data.goalProgressRate
                            tvDetailLessonName.text = it.data.lessonName
                            tvDetailLessonMessageInfo.text = it.data.message
                            totalNumber = it.data.totalNumber.toString()
                            tvDetailLessonCountInfo.text = totalNumber
                            tvDetailLessonPriceInfo.text = it.data.price.toString()
                            presentNumber = it.data.presentNumber.toString()
                            tvDetailLessonPresentNumber.text =
                                "내가 들은 강의: ${totalNumber}개 중 ${presentNumber}개"

                            price = it.data.wastePrice.toString().toInt()

                            val df = DecimalFormat("#,###")
                            val changedPriceFormat = df.format(price)

                            tvDetailLessonLoseMoneyInfo.text = "${changedPriceFormat}원"
                            tvDetailLessonEndDate.text = "목표 완강일: $endDateInfo"

                            tvDetailLessonRemainDay.text = "D-${it.data.remainDay}"

                            if (it.data.remainDay <= 10) {
                                tvDetailLessonWarning.visibility = View.VISIBLE
                            }
                        }
                    }

                    is LessonDetailState.Error -> {
                        Log.d("fetch Error", "${it.exception}")
                    }


                }
            }
        }

        btnDetailLessonUpdate.setOnClickListener {
            //intent, lessonId
        }

        btnDetailLessonRemove.setOnClickListener {

        }

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