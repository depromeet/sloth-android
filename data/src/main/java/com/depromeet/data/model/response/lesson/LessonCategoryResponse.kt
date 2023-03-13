package com.depromeet.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonCategoryResponse(
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("categoryName")
    val categoryName: String
) {
    companion object {
        val EMPTY = LessonCategoryResponse(0, "")
    }
}


