package com.depromeet.presentation.ui.onboarding

import com.depromeet.presentation.model.TodayLesson


sealed class OnBoardingUiModel {
    object OnBoardingEmptyItem : OnBoardingUiModel()
    data class OnBoardingHeaderItem(val itemType: OnBoardingType) : OnBoardingUiModel()
    data class OnBoardingTitleItem(val itemType: OnBoardingType) : OnBoardingUiModel()
    data class OnBoardingDoingItem(val onBoardingItem: TodayLesson) : OnBoardingUiModel()
    data class OnBoardingFinishedItem(val onBoardingItem: TodayLesson) : OnBoardingUiModel()
}