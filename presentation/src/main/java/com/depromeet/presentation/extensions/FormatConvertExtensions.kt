package com.depromeet.presentation.extensions

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


fun getPickerDateToDash(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    return formatter.format(date)
}

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

fun changeDateFormat(date: String): String {
    val dateArr = date.split("-")
    val yearOfDate = dateArr[0]
    val monthOfDate = dateArr[1]
    val dayOfDate = dateArr[2]

    return "${yearOfDate}.${monthOfDate}.$dayOfDate"
}

fun changeStringToDot(date: String): String {
    val dateList = date.split("-")

    val yearOfDate = dateList[0]
    val monthOfDate = dateList[1]
    val dayOfDate = dateList[2]

    return "${yearOfDate}.${monthOfDate}.${dayOfDate}"
}

fun addLeadingZero(data: String): String {
    var tmp = data
    if (tmp.length == 1) {
        tmp = "0$tmp"
    }
    return tmp
}