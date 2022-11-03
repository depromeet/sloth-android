package com.depromeet.sloth.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.databinding.ActivityLessonDetailBinding
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.common.UiState
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
            lifecycleScope.launch {
                lessonDetailState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress(this@LessonDetailActivity)
                            is UiState.Success<LessonDetail> -> viewModel.setLessonDetailInfo(
                                uiState.data
                            )
                            is UiState.Unauthorized -> showLogoutDialog(this@LessonDetailActivity) { viewModel.removeAuthToken() }
                            is UiState.Error -> Timber.tag("fetch Error").d(uiState.throwable)
                            else -> {}
                        }
                        hideProgress()
                    }
            }

            lifecycleScope.launch {
                lessonDeleteState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress(this@LessonDetailActivity)
                            is UiState.Success<LessonDeleteResponse> -> {
                                showToast(getString(R.string.lesson_delete_complete))
                                finish()
                            }
                            is UiState.Unauthorized -> showLogoutDialog(this@LessonDetailActivity) { viewModel.removeAuthToken() }
                            is UiState.Error -> {
                                Timber.tag("fetch Error").d(uiState.throwable)
                                showToast(getString(R.string.lesson_delete_fail))
                            }
                            else -> {}
                        }
                        hideProgress()
                    }
            }

            lifecycleScope.launch {
                lessonDetail
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { lessonDetail ->
                        binding.lessonDetail = lessonDetail
                    }
            }

            lifecycleScope.launch {
                lessonUpdateClick
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
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

            lifecycleScope.launch {
                lessonDeleteClick
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { showLessonDeleteDialog() }
            }
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