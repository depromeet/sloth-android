package com.depromeet.sloth.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.extensions.handleLoadingState
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.common.EventObserver
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LessonDetailActivity : BaseActivity<ActivityLessonDetailBinding>(R.layout.activity_lesson_detail) {

    private val viewModel: LessonDetailViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.apply {
            lessonDetailState.observe(this@LessonDetailActivity) { lessonDetailState ->
                when (lessonDetailState) {
                    is LessonDetailState.Loading -> {
                        handleLoadingState(this@LessonDetailActivity)
                    }

                    is LessonDetailState.Success<LessonDetailResponse> -> {
                        Timber.tag("fetch Success").d("${lessonDetailState.data}")

                        handleSuccessState(lessonDetailState.data)
                    }

                    is LessonDetailState.Unauthorized -> {
                        showLogoutDialog(this@LessonDetailActivity,
                            this@LessonDetailActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonDetailState.Error -> {
                        Timber.tag("Error").d(lessonDetailState.exception)
                    }
                }
                hideProgress()
            }

            lessonDeleteState.observe(this@LessonDetailActivity) { lessonDeleteState ->
                when (lessonDeleteState) {
                    is LessonDeleteState.Loading -> showProgress(this@LessonDetailActivity)

                    is LessonDeleteState.Success<LessonDeleteResponse> -> handleSuccessState(
                        lessonDeleteState.data)

                    is LessonDeleteState.Unauthorized -> {
                        showLogoutDialog(this@LessonDetailActivity,
                            this@LessonDetailActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonDeleteState.Error -> {
                        Timber.tag("fetch Error").d(lessonDeleteState.exception)
                        showToast("강의를 삭제하지 못했어요")
                    }
                }
                hideProgress()
            }

            lessonDetail.observe(this@LessonDetailActivity) { lessonDetail ->
                binding.lessonDetail = lessonDetail
            }

            lessonUpdateEvent.observe(this@LessonDetailActivity, EventObserver { lessonDetail ->
                startActivity(
                    Intent(this@LessonDetailActivity, UpdateLessonActivity::class.java).apply {
                        putExtra("lessonDetail", lessonDetail)
                    }
                )
            })

            lessonDeleteEvent.observe(this@LessonDetailActivity, EventObserver {
                showLessonDeleteDialog()
            })
        }

        initViews()

        bind {
            vm = viewModel
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.fetchLessonDetail()
    }

    private fun <T> handleSuccessState(data: T) {
        if (data is LessonDeleteResponse) {
            showToast("강의가 삭제 되었어요")
            finish()
        }

        if (data is LessonDetailResponse) {
            viewModel.setLessonDetailInfo(data)
        }
    }

    override fun initViews() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }
    }

    private fun showLessonDeleteDialog() {
        val dlg = SlothDialog(this@LessonDetailActivity, DialogState.DELETE_LESSON)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                //viewModel.deleteLesson(lessonId)
                viewModel.deleteLesson()
            }
        }
        dlg.start()
    }

    companion object {
        const val LESSON_ID = "lessonId"
    }
}