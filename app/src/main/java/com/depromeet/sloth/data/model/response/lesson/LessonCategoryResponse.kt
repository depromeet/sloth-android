package com.depromeet.sloth.data.model.response.lesson

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
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
