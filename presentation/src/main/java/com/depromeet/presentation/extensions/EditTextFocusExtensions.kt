package com.depromeet.presentation.extensions

import android.app.Activity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.depromeet.presentation.R


fun setEditTextFocus(activity: Activity, editText: EditText) {
    editText.setOnFocusChangeListener { _, gainFocus ->
        if (gainFocus) {
            editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
        } else {
            editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
        }
    }
    clearEditTextFocus(activity, editText)
}

fun clearEditTextFocus(activity: Activity, editText: EditText) {
    editText.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            editText.clearFocus()
            hideKeyBoard(activity)
        }
        false
    }
}

