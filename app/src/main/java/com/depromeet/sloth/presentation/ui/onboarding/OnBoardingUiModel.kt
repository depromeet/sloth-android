package com.depromeet.sloth.presentation.ui.onboarding

import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse

sealed class OnBoardingUiModel {
    object OnBoardingEmptyItem : OnBoardingUiModel()
    data class OnBoardingHeaderItem(val itemType: OnBoardingType) : OnBoardingUiModel()
    data class OnBoardingTitleItem(val itemType: OnBoardingType) : OnBoardingUiModel()
    data class OnBoardingDoingItem(val onBoardingItem: TodayLessonResponse) : OnBoardingUiModel()
    data class OnBoardingFinishedItem(val onBoardingItem: TodayLessonResponse) : OnBoardingUiModel()
}