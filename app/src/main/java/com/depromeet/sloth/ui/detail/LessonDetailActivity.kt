package com.depromeet.sloth.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.extensions.handleLoadingState
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.common.EventObserver
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import com.depromeet.sloth.util.LESSON_DETAIL
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LessonDetailActivity :
    BaseActivity<ActivityLessonDetailBinding>(R.layout.activity_lesson_detail) {

    private val viewModel: LessonDetailViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = viewModel
        }

        initObserver()
        initViews()
    }

    private fun initObserver() {
        viewModel.apply {
            lessonDetailState.observe(this@LessonDetailActivity) { lessonDetailState ->
                when (lessonDetailState) {
                    is LessonState.Loading -> {
                        handleLoadingState(this@LessonDetailActivity)
                    }

                    is LessonState.Success<LessonDetail> -> {
                        Timber.tag("fetch Success").d("${lessonDetailState.data}")
                        viewModel.setLessonDetailInfo(lessonDetailState.data)
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(this@LessonDetailActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Timber.tag("fetch Error").d(lessonDetailState.throwable)
                    }

                    else -> {}
                }
                hideProgress()
            }

            lessonDeleteState.observe(this@LessonDetailActivity) { lessonDeleteState ->
                when (lessonDeleteState) {
                    is LessonState.Loading -> showProgress(this@LessonDetailActivity)

                    is LessonState.Success<LessonDeleteResponse> -> {
                        showToast(getString(R.string.lesson_delete_complete))
                        finish()
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(this@LessonDetailActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Timber.tag("fetch Error").d(lessonDeleteState.throwable)
                        showToast(getString(R.string.lesson_delete_fail))
                    }

                    else -> {}
                }
                hideProgress()
            }

            lessonDetail.observe(this@LessonDetailActivity) { lessonDetail ->
                binding.lessonDetail = lessonDetail
            }

            lessonUpdateEvent.observe(this@LessonDetailActivity, EventObserver { lessonDetail ->
                startActivity(
                    Intent(this@LessonDetailActivity, UpdateLessonActivity::class.java).apply {
                        putExtra(LESSON_DETAIL, lessonDetail)
                    }
                )
            })

            lessonDeleteEvent.observe(this@LessonDetailActivity, EventObserver {
                showLessonDeleteDialog()
            })
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchLessonDetail()
    }

    override fun initViews() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }
    }

    private fun showLessonDeleteDialog() {
        val dlg = SlothDialog(this@LessonDetailActivity, DialogState.DELETE_LESSON)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                viewModel.deleteLesson()
            }
        }
        dlg.start()
    }

    companion object {
        const val LESSON_ID = "lessonId"
    }
}