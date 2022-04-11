package com.depromeet.sloth.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LessonDetailActivity : BaseActivity<ActivityLessonDetailBinding>() {

    private val viewModel: LessonDetailViewModel by viewModels()

    lateinit var lessonId: String
    lateinit var lesson: Lesson

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

        binding.lifecycleOwner = this

        viewModel.apply {
            lessonDetailState.observe(this@LessonDetailActivity) { lessonDetailState ->
                when (lessonDetailState) {
                    is LessonDetailState.Loading -> {
                        Log.d("LessonDetailActivity", "onCreate: LessonDetailState.Loading 호출")
                        handleLoadingState(this@LessonDetailActivity)
                    }

                    is LessonDetailState.Success<LessonDetailResponse> -> {
                        Log.d("fetch Success", "${lessonDetailState.data}")

                        handleSuccessState(lessonDetailState.data)
                    }

                    is LessonDetailState.Unauthorized -> {
                        showLogoutDialog()
                    }

                    is LessonDetailState.NotFound, LessonDetailState.Forbidden -> {
                        Toast.makeText(this@LessonDetailActivity,
                            "강의 상세 정보를 가져오지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()
                    }

                    is LessonDetailState.Error -> {
                        Log.d("Error", "${lessonDetailState.exception}")
                    }
                }
                hideProgress()
            }

            lessonDeleteState.observe(this@LessonDetailActivity) { lessonDeleteState ->
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

            lessonDetail.observe(this@LessonDetailActivity) { lessonDetail ->
                binding.lessonDetail = lessonDetail
            }
        }

        initViews()
    }

    override fun onStart() {
        super.onStart()

        handleLoadingState(this@LessonDetailActivity)
        viewModel.fetchLessonDetail(lessonId)
    }

    private fun <T> handleSuccessState(data: T) {
        if (data is LessonDeleteResponse) {
            Toast.makeText(this@LessonDetailActivity, "강의가 삭제 되었어요", Toast.LENGTH_SHORT).show()
            finish()
        }

        if (data is LessonDetailResponse) {
            viewModel.setLessonInfo(data)
            setLessonDetailInfo(data)
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

    private fun setLessonDetailInfo(data: LessonDetailResponse) = with(binding) {
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
    }
}