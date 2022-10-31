package com.depromeet.sloth.data.network.lesson

import androidx.annotation.Keep

@Keep
data class LessonCategory(
    var categoryId: Int,
    var categoryName: String
) {
    companion object {
        val EMPTY = LessonCategory(0, "")
    }
}
