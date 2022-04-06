package com.depromeet.sloth.extensions

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R

fun focusInputForm(editText: EditText, button: AppCompatButton, context: Context) {
    editText.addTextChangedListener(object : TextWatcher {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {

        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun afterTextChanged(editable: Editable?) {
            setButton(editable, button, context)
        }
    })
    setEditTextFocus(editText)
}

fun setEditTextFocus(editText: EditText) {
    editText.setOnFocusChangeListener { _, gainFocus ->
        if (gainFocus) {
            editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
        } else {
            editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
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