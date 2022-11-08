package com.depromeet.sloth.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.common.Result
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import com.depromeet.sloth.util.LESSON_DETAIL
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class LessonDetailActivity :
    BaseActivity<ActivityLessonDetailBinding>(R.layout.activity_lesson_detail) {

    private val lessonDetailViewModel: LessonDetailViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = lessonDetailViewModel
        }
        initListener()
        initObserver()
    }

    override fun onStart() {
        super.onStart()
        lessonDetailViewModel.fetchLessonDetail()
    }

    private fun initObserver() = with(lessonDetailViewModel) {
        lifecycleScope.launch {
            repeatOnStarted {
                launch {
                    lessonDetailState
                        .collect { result ->
                            when (result) {
                                is Result.Loading -> showProgress(this@LessonDetailActivity)
                                is Result.Success<LessonDetail> -> {
                                    lessonDetailViewModel.setLessonDetailInfo(result.data)
                                }
                                is Result.Unauthorized -> showForbiddenDialog(this@LessonDetailActivity) { lessonDetailViewModel.removeAuthToken() }
                                is Result.Error -> Timber.tag("fetch Error").d(result.throwable)
                                else -> {}
                            }
                            hideProgress()
                        }
                }

                launch {
                    lessonDeleteState
                        .collect { result ->
                            when (result) {
                                is Result.Loading -> showProgress(this@LessonDetailActivity)
                                is Result.Success<LessonDeleteResponse> -> {
                                    showToast(getString(R.string.lesson_delete_complete))
                                    finish()
                                }
                                is Result.Unauthorized -> showForbiddenDialog(this@LessonDetailActivity) { lessonDetailViewModel.removeAuthToken() }
                                is Result.Error -> {
                                    Timber.tag("fetch Error").d(result.throwable)
                                    showToast(getString(R.string.lesson_delete_fail))
                                }
                                else -> {}
                            }
                            hideProgress()
                        }
                }

                launch {
                    lessonUpdateClick
                        .collect { lessonDetail ->
                            startActivity(
                                Intent(
                                    this@LessonDetailActivity,
                                    UpdateLessonActivity::class.java
                                ).apply {
                                    putExtra(LESSON_DETAIL, lessonDetail)
                                }
                            )
                        }
                }

                launch {
                    lessonDeleteClick
                        .collect {
                            showLessonDeleteDialog()
                        }
                }
            }
        }
    }

    private fun initListener() = with(binding) {
        tbDetailLesson.setNavigationOnClickListener { finish() }
    }

    private fun showLessonDeleteDialog() {
        val dlg = SlothDialog(this@LessonDetailActivity, DialogState.DELETE_LESSON)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                lessonDetailViewModel.deleteLesson()
            }
        }
        dlg.start()
    }
}