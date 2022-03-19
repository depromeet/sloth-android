package com.depromeet.sloth.util

import android.content.Context
import android.util.TypedValue

/**
 * @author 최철훈
 * @created 2022-03-19
 * @desc Context를 파라미터로 받는 Util 클래스
 */
object ContextUtil {

    fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), context.resources.displayMetrics
        ).toInt()
    }
}