package com.depromeet.sloth.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.databinding.FragmentLessonDetailBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.extensions.showToast
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LessonDetailFragment :
    BaseFragment<FragmentLessonDetailBinding>(R.layout.fragment_lesson_detail) {

    private val lessonDetailViewModel: LessonDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = lessonDetailViewModel
        }
        initListener()
        initObserver()
    }

    private fun initObserver() = with(lessonDetailViewModel) {
        repeatOnStarted {
            launch {
                fetchLessonDetailEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<LessonDetailResponse> -> {
                                binding.lessonDetailNetworkError.itemNetworkError.visibility = View.GONE
                                setLessonDetailInfo(result.data)
                            }

                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> {
                                        showForbiddenDialog(
                                            requireContext(),
                                            this@LessonDetailFragment
                                        ) {
                                            removeAuthToken()
                                        }
                                    }
                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(
                                            requireContext(),
                                            getString(R.string.lesson_detail_info_fail)
                                        )
                                        binding.lessonDetailNetworkError.itemNetworkError.visibility = View.VISIBLE
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
                                showToast(
                                    requireContext(),
                                    getString(R.string.lesson_delete_complete)
                                )
                                val action = LessonDetailFragmentDirections.actionLessonDetailToLessonList()
                                findNavController().safeNavigate(action)
                            }

                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(
                                        requireContext(),
                                        this@LessonDetailFragment
                                    ) {
                                        removeAuthToken()
                                    }

                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(
                                            requireContext(),
                                            getString(R.string.lesson_delete_fail)
                                        )
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                navigateToUpdateLessonEvent
                    .collect { lessonDetail ->
                        val action =
                            LessonDetailFragmentDirections.actionLessonDetailToUpdateLesson(
                                lessonDetail
                            )
                        findNavController().safeNavigate(action)
                    }
            }

            launch {
                navigateToDeleteLessonDialogEvent
                    .collect {
                        showLessonDeleteDialog()
                    }
            }

//            launch {
//                isLoading.collect {
//                    if (it) showProgress() else hideProgress()
//                }
//            }
        }
    }

    private fun initListener() = with(binding) {
        tbLessonDetail.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun showLessonDeleteDialog() {
        val dlg = SlothDialog(requireContext(), DialogState.DELETE_LESSON)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                lessonDetailViewModel.deleteLesson()
            }
        }
        dlg.show()
    }
}