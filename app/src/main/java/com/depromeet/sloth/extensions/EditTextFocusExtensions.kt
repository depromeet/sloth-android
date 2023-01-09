package com.depromeet.sloth.extensions

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.depromeet.sloth.R

fun setEditTextFocus(editText: EditText) {
    editText.setOnFocusChangeListener { _, gainFocus ->
        if (gainFocus) {
            editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
        } else {
            editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
        }
    }
    clearEditTextFocus(editText)
}

fun clearEditTextFocus(editText: EditText) {
    editText.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            editText.clearFocus()
        }
        false
    }
}

