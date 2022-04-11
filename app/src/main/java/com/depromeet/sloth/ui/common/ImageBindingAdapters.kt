package com.depromeet.sloth.ui.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.depromeet.sloth.GlideApp
import com.depromeet.sloth.R

@BindingAdapter("currentProgressRate", "goalProgressRate")
fun setLessonSummaryImage(view: ImageView, currentProgressRate: Float, goalProgressRate: Float) {
    // 시작 하지 않음
    if(goalProgressRate == 0F) {
        GlideApp.with(view.context).load(R.drawable.ic_detail_sloth_not_started_yet).into(view)
    }
    // 시작함
    else {
        if (currentProgressRate >= goalProgressRate) {
            GlideApp.with(view.context).load(R.drawable.ic_detail_sloth_steadily_listen).into(view)
        } else {
            GlideApp.with(view.context).load(R.drawable.ic_detail_sloth_fail_goal).into(view)
        }
    }

}


