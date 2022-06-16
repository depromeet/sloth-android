package com.depromeet.sloth.data.model

data class LessonCategory(
    var categoryId: Int,
    var categoryName: String
) {
    companion object {
        val EMPTY = LessonCategory(0, "")
    }

}
