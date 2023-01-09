package com.depromeet.sloth.presentation.custom

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.util.ContextUtil

class LessonItemDecoration(
    context: Context,
    dp: Int
) : RecyclerView.ItemDecoration() {

    private var fullDp = ContextUtil.dpToPx(context, dp)
    private var halfDp = ContextUtil.dpToPx(context, fullDp / 2)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

//        val startIndex = 0
//        val endIndex = parent.itemDecorationCount - 1
//        val position = parent.getChildAdapterPosition(view)

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