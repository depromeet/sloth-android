package com.depromeet.sloth.ui.home

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LessonItemDecoration(
    context: Context,
    dp: Int
) : RecyclerView.ItemDecoration() {

    private var fullDp = dpToPx(context, dp)
    private var halfDp = dpToPx(context, fullDp / 2)

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        )
            .toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val startIndex = 0
        val endIndex = parent.itemDecorationCount - 1
        val position = parent.getChildAdapterPosition(view)

        outRect.bottom = fullDp

//        when (position) {
//            startIndex -> {
//                outRect.bottom = halfDp
//            }
//            endIndex -> {
//                outRect.top = halfDp
//            }
//            else -> {
//                outRect.top = halfDp
//                outRect.bottom = halfDp
//            }
//        }
    }
}