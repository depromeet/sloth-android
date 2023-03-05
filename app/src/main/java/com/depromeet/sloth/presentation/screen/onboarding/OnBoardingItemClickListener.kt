package com.depromeet.sloth.presentation.screen.onboarding

data class OnBoardingItemClickListener(
    val onClick: () -> Unit,
    val onPlusClick: () -> Unit,
    val onMinusClick: () -> Unit,
    val onFinishClick: () -> Unit
)
