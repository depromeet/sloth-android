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
import androidx.room.Delete
import com.depromeet.sloth.R
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.detail.LessonDetailState
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.ui.base.BaseActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        lessonId = "80"

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


        btnDetailUpdateLesson.setOnClickListener {
            //UpdateLessonActivity.newIntent(this, lessonId, lessonModel)
        }

        btnDetailDeleteLesson.setOnClickListener {
            MaterialAlertDialogBuilder(this@LessonDetailActivity)
                .setMessage(getString(R.string.delete_dialog_message))
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    deleteLesson(accessToken, lessonId)
                    Toast.makeText(this@LessonDetailActivity, "강의가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    finish() /*화면 종료*/
                }
                .show()
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

            tvDetailLessonCategory.text = data.categoryName

            pbDetailCurrentLessonProgress.labelText = "${data.currentProgressRate}%"
            pbDetailCurrentLessonProgress.progress = data.currentProgressRate.toFloat()

            pbDetailGoalLessonProgress.labelText = "${data.goalProgressRate}%"
            pbDetailGoalLessonProgress.progress = data.goalProgressRate.toFloat()

            startDate = data.startDate
            startDateInfo = changeDateFormat(startDate)

            endDate = data.endDate
            endDateInfo = changeDateFormat(endDate)

            tvDetailLessonPeriodInfo.text = "$startDateInfo - $endDateInfo"
            tvDetailLessonName.text = data.lessonName
            tvDetailLessonMessageInfo.text = data.message
            totalNumber = data.totalNumber.toString()
            tvDetailLessonCountInfo.text = totalNumber

            presentNumber = data.presentNumber.toString()
            tvDetailLessonPresentNumber.text =
                "내가 들은 강의: ${totalNumber}개 중 ${presentNumber}개"

            tvDetailLessonLoseMoneyInfo.text = changeDecimalFormat(data.wastePrice)

            tvDetailLessonPriceInfo.text = changeDecimalFormat(data.price)

            tvDetailLessonEndDate.text = "목표 완강일: $endDateInfo"

            tvDetailLessonRemainDay.text = "D-${data.remainDay}"

            if (data.remainDay <= 10) {
                tvDetailLessonWarning.visibility = View.VISIBLE
            }
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