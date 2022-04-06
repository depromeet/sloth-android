package com.depromeet.sloth.extensions

import android.content.Context
import android.os.Build
import android.text.Editable
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R

@RequiresApi(Build.VERSION_CODES.M)
fun unlockButton(button: AppCompatButton, context: Context) {
    button.apply {
        isEnabled = true
        background = AppCompatResources.getDrawable(
            context, R.drawable.bg_register_rounded_button_sloth
        )
    }
}

@RequiresApi(Build.VERSION_CODES.M)
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