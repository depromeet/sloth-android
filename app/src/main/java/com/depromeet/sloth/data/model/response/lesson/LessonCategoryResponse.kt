package com.depromeet.sloth.data.model.response.lesson

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LessonCategoryResponse(
    @SerializedName("categoryId")
    var categoryId: Int,
    @SerializedName("categoryName")
    var categoryName: String
) {
    companion object {
        val EMPTY = LessonCategoryResponse(0, "")
    }
}
