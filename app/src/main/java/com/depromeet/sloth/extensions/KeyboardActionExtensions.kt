package com.depromeet.sloth.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun hideKeyBoard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}