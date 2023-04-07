package com.depromeet.presentation.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import com.depromeet.presentation.R
import com.depromeet.presentation.extensions.changeDateFormatToDot
import com.skydoves.progressview.ProgressView
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// TODO 바인딩 어댑터 함수들 단일 책임의 원칙을 만족하도록 수정
// TODO visibility 관련 작업은 xml에서 직접

@BindingAdapter("onSingleClick")
fun bindOnSingleClick(view: View, onClickListener: View.OnClickListener) {
    view.setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View) {
            onClickListener.onClick(v)
        }
    })
}

@BindingAdapter("currentProgressRate", "goalProgressRate")
fun setLessonSummaryImage(view: ImageView, currentProgressRate: Float, goalProgressRate: Float) {
    // 시작 하지 않음
    if (goalProgressRate == 0F) {
        GlideApp.with(view.context).load(R.drawable.ic_lesson_detail_sloth_not_started_yet).into(view)
    }
    // 시작함
    else {
        if (currentProgressRate >= goalProgressRate) {
            GlideApp.with(view.context).load(R.drawable.ic_lesson_detail_sloth_steadily_listen).into(view)
        } else {
            GlideApp.with(view.context).load(R.drawable.ic_lesson_detail_sloth_fail_goal).into(view)
        }
    }
}

@BindingAdapter("isVisible")
fun isVisible(view: TextView, goalProgressRate: Float) = with(view) {
    visibility = if (goalProgressRate == 0F) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("currentProgressRate", "goalProgressRate")
fun setLessonSummaryText(view: TextView, currentProgressRate: Float, goalProgressRate: Float) =
    with(view) {
        text = if (goalProgressRate == 0F) view.context.getString(R.string.not_started_yet)
        else {
            if (currentProgressRate >= goalProgressRate) {
                view.context.getString(R.string.mission_success)
            } else {
                view.context.getString(R.string.mission_fail)
            }
        }
    }

@BindingAdapter("lessonTotalNumberHint")
fun setLessonTotalNumberFormat(view: EditText, totalNumber: Int) = with(view) {
    hint = view.context.getString(R.string.unit_lesson_total_number, totalNumber)
}


@BindingAdapter("goalProgressRate", "priceFormat")
fun setWastePriceFormat(view: TextView, goalProgressRate: Float, price: Int) = with(view) {
    if (goalProgressRate == 0F) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        val decimalFormat = DecimalFormat("#,###")
        text = view.context.getString(R.string.unit_lesson_price, decimalFormat.format(price))
    }
}

@BindingAdapter("priceFormat")
fun setPriceFormat(view: TextView, price: Int) = with(view) {
    val decimalFormat = DecimalFormat("#,###")
    text = context.getString(R.string.unit_lesson_price, decimalFormat.format(price))
}

@BindingAdapter("lessonPriceHint")
fun setLessonPriceHint(view: EditText, price: Int) = with(view) {
    val decimalFormat = DecimalFormat("#,###")
    hint = context.getString(R.string.unit_lesson_price, decimalFormat.format(price))
}

@SuppressLint("NewApi")
@BindingAdapter("lessonDate")
fun setLessonDate(view: TextView, lessonDate: ZonedDateTime) {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    view.text = lessonDate.format(formatter)
}

@BindingAdapter("goalProgressRate", "d_day")
fun setRemainDayFormat(view: TextView, goalProgressRate: Float, d_day: Int) = with(view) {
    if (goalProgressRate == 0F) {
        visibility = View.GONE
    } else {
        if (d_day < 0) {
            visibility = View.VISIBLE
            text = view.context.getString(R.string.d_day_plus_format, d_day * -1)
        } else {
            if (d_day == 0) {
                visibility = View.VISIBLE
                text = view.context.getString(R.string.d_day)
            } else {
                if (d_day < 10) {
                    view.setTextColor(Color.RED)
                }
                visibility = View.VISIBLE
                text = view.context.getString(R.string.d_day_minus_format, d_day)
            }
        }
    }
}

@BindingAdapter("setProgressRate")
fun setProgressRate(view: ProgressView, progressRate: Float) = with(view) {
    labelText = view.context.getString(R.string.unit_lesson_take_rate, progressRate.toInt())
    progress = progressRate
}

@BindingAdapter("total", "present")
fun setPresentLessonProgress(view: TextView, total: Int, present: Int) = with(view) {
    text = context.getString(R.string.current_lesson_progress, total, present)
}

@BindingAdapter("goalProgressRate", "remainDay")
fun showLessonState(view: TextView, goalProgressRate: Float, remainDay: Int) = with(view) {
    if (goalProgressRate == 0f) {
        visibility = View.GONE
    } else {
        when {
            remainDay in 0..10 -> {
                visibility = View.VISIBLE
                background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_rounded_chip_caution)
                text = view.context.getString(R.string.lesson_warning)
            }
            remainDay < 0 -> {
                visibility = View.VISIBLE
                background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_rounded_chip_black)
                text = view.context.getString(R.string.lesson_close)
            }
            else -> {
                visibility = View.INVISIBLE
            }
        }
    }
}

@BindingAdapter("lessonDate")
fun setLessonDate(view: TextView, date: ArrayList<Int>?) = with(view) {
    if (date.isNullOrEmpty()) return
    else {
        text = context.getString(
            R.string.lesson_end_date_info,
            changeDateFormatToDot(date)
        )
    }
}

@BindingAdapter("startDate", "endDate")
fun setLessonPeriod(view: TextView, startDate: ArrayList<Int>?, endDate: ArrayList<Int>?) =
    with(view) {
        if (!startDate.isNullOrEmpty() and !endDate.isNullOrEmpty()) {
            text = context.getString(
                R.string.lesson_period_info,
                changeDateFormatToDot(startDate!!),
                changeDateFormatToDot(endDate!!)
            )
        } else return
    }

@BindingAdapter("email", "isEmailProvided")
fun setUserEmail(view: TextView, email: String, isEmailProvided: Boolean) =
    with(view) {
        text = if (isEmailProvided) {
            email
        } else {
            DEFAULT_STRING_VALUE
        }
    }

