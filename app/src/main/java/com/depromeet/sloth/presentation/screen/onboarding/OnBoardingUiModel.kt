package com.depromeet.sloth.presentation.screen.onboarding

import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse

sealed class OnBoardingUiModel {
    object OnBoardingEmptyItem : OnBoardingUiModel()
    data class OnBoardingHeaderItem(val itemType: OnBoardingType) : OnBoardingUiModel()
    data class OnBoardingTitleItem(val itemType: OnBoardingType) : OnBoardingUiModel()
    data class OnBoardingDoingItem(val todayLesson: TodayLessonResponse) : OnBoardingUiModel()
    data class OnBoardingFinishedItem(val todayLesson: TodayLessonResponse) : OnBoardingUiModel()
}