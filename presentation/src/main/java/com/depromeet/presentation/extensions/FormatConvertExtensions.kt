package com.depromeet.presentation.extensions

import java.text.DecimalFormat


fun changeDecimalFormat(data: Int): String {
    val decimalFormant = DecimalFormat("#,###")
    val changedPriceFormat = decimalFormant.format(data)

    return "${changedPriceFormat}원"
}

fun changeDateFormatToDot(date: ArrayList<String>): String {
    val yearOfDate = date[0]
    val monthOfDate = addLeadingZero(date[1])
    val dayOfDate = addLeadingZero(date[2])

    return "${yearOfDate}. ${monthOfDate}. $dayOfDate"
}

fun addLeadingZero(data: String): String {
    var tmp = data
    if (tmp.length == 1) {
        tmp = "0$tmp"
    }
    return tmp
}