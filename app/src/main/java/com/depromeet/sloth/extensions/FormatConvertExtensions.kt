package com.depromeet.sloth.extensions

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun getPickerDateToDash(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    return formatter.format(date)
}

fun changeDateFormatToDash(date: ArrayList<String>): String {
    val yearOfDate = date[0]
    val monthOfDate = addLeadingZero(date[1])
    val dayOfDate = addLeadingZero(date[2])

    return "${yearOfDate}-${monthOfDate}-$dayOfDate"
}

fun getPickerDateToDot(date: Date): String {
    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

    return formatter.format(date)
}

fun changeDecimalFormat(data: Int): String {
    val df = DecimalFormat("#,###")
    val changedPriceFormat = df.format(data)

    return "${changedPriceFormat}원"
}

fun changeDateFormatToDot(date: ArrayList<String>): String {
    val yearOfDate = date[0]
    val monthOfDate = addLeadingZero(date[1])
    val dayOfDate = addLeadingZero(date[2])

    return "${yearOfDate}. ${monthOfDate}. $dayOfDate"
}

fun changeListToDot(date: ArrayList<String>): String {
    val yearOfDate = date[0]
    val monthOfDate = date[1]
    val dayOfDate = date[2]

    return "${yearOfDate}.${monthOfDate}.${dayOfDate}"
}

fun changeDateFormat(date: String): String {
    val dateArr = date.split("-")
    val yearOfDate = dateArr[0]
    val monthOfDate = dateArr[1]
    val dayOfDate = dateArr[2]

    return "${yearOfDate}.${monthOfDate}.$dayOfDate"
}

fun changeDateStringToArrayList(date: String): ArrayList<String> {
    val dateList = date.split("-")
    return ArrayList(dateList)
}

fun addLeadingZero(data: String): String {
    var tmp = data
    if (tmp.length == 1) {
        tmp = "0$tmp"
    }
    return tmp
}

@SuppressLint("SimpleDateFormat")
fun stringToDate(string: String): Date? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    return dateFormat.parse(string)
}
