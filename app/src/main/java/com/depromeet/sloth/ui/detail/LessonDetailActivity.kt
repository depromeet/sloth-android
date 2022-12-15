package com.depromeet.sloth.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.update.UpdateLessonActivity
import com.depromeet.sloth.util.LESSON_DETAIL
import com.depromeet.sloth.util.Result
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
                    fetchLessonDetailEvent
                        .collect { result ->
                            when (result) {
                                is Result.Loading -> showProgress()
                                is Result.UnLoading -> hideProgress()
                                is Result.Success<LessonDetailResponse> -> {
                                    lessonDetailViewModel.setLessonDetailInfo(result.data)
                                }
                                is Result.Error -> {
                                    when (result.statusCode) {
                                        401 -> {
                                            showForbiddenDialog(this@LessonDetailActivity) {
                                                lessonDetailViewModel.removeAuthToken()
                                            }
                                        }
                                        else -> {
                                            Timber.tag("Fetch Error").d(result.throwable)
                                            showToast(getString(R.string.lesson_detail_info_fail))
                                        }
                                    }
                                }
                            }
                        }
                }

                launch {
                    deleteLessonEvent
                        .collect { result ->
                            when (result) {
                                is Result.Loading -> showProgress()
                                is Result.UnLoading -> hideProgress()
                                is Result.Success<LessonDeleteResponse> -> {
                                    showToast(getString(R.string.lesson_delete_complete))
                                    finish()
                                }
                                is Result.Error -> {
                                    when (result.statusCode) {
                                        401 -> showForbiddenDialog(this@LessonDetailActivity) {
                                            lessonDetailViewModel.removeAuthToken()
                                        }
                                        else -> {
                                            Timber.tag("Fetch Error").d(result.throwable)
                                            showToast(getString(R.string.lesson_delete_fail))
                                        }
                                    }
                                }
                            }
                        }
                }

                launch {
                    navigateToUpdateLessonEvent
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
                    showDeleteLessonDialogEvent
                        .collect {
                            showLessonDeleteDialog()
                        }
                }

//                launch {
//                    isLoading.collect {
//                        if (it) showProgress() else hideProgress()
//                    }
//                }
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
        dlg.show()
    }
}