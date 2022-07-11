package com.depromeet.sloth.data.network.lesson.list

/**
 * @author 최철훈
 * @created 2022-06-01
 * @desc
 */

data class LessonFinishResponse (
    var isFinished: Boolean
) {
    companion object {
        val EMPTY = LessonFinishResponse(false)
    }
}