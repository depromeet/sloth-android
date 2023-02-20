package com.depromeet.sloth.presentation.screen.todaylesson

import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse

sealed class OnBoardingUiModel {

    data class OnBoardingHeaderItem(val itemType: TodayLessonType) : OnBoardingUiModel()

    data class OnBoardingTitleItem(val itemType: TodayLessonType) : OnBoardingUiModel()

    data class OnBoardingDoingItem(val todayLesson: TodayLessonResponse) : OnBoardingUiModel()

    data class OnBoardingFinishedItem(val todayLesson: TodayLessonResponse) : OnBoardingUiModel()
}