package com.depromeet.sloth.extensions

import android.content.Context
import android.text.Editable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R

//TODO lock, unlock 버튼 뷰모델내에 LiveData 로 선언하여 데이터바인딩
fun unlockButton(button: AppCompatButton, context: Context) {
    button.apply {
        isEnabled = true
        background = AppCompatResources.getDrawable(
            context, R.drawable.bg_register_rounded_button_sloth
        )
    }
}

fun lockButton(button: AppCompatButton, context: Context) {
    button.apply {
        isEnabled = false
        background = AppCompatResources.getDrawable(
            context, R.drawable.bg_register_rounded_button_disabled
        )
    }
}

fun setButton(editable: Editable?, button: AppCompatButton, context: Context) {
    if (editable.isNullOrEmpty()) {
        lockButton(button, context)
    } else {
        unlockButton(button, context)
    }
}


