package com.kashtansystem.project.gloriyamarketing.utils

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toBeginDay() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Calendar.toEndDay() {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 59)
}

fun AppCompatActivity.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.toggleKeyboard(view: View) {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
    inputManager.toggleSoftInputFromWindow(view.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0);
}

const val DEFAULT_DECIMAL_FORMAT = "0,000.##"
const val DEFAULT_DATE_FORMAT = "dd.MM.yyyy"

fun Float.formatDecimals(format: String = DEFAULT_DECIMAL_FORMAT): String {
    val df = DecimalFormat(format)
    return df.format(this)
}

fun Double.formatDecimals(): String {
    val decimalFormat: DecimalFormat
    val symbols = DecimalFormatSymbols(Locale.getDefault())
    symbols.groupingSeparator = ' '
    decimalFormat = DecimalFormat("#,###.##", symbols)
    return decimalFormat.format(this)
}

fun BigDecimal.formatDecimals(scaleDigit: Int = 2, roundingMode: Int = BigDecimal.ROUND_DOWN): String {
    val bd = setScale(scaleDigit, roundingMode)
    val df = DecimalFormat()
    df.maximumFractionDigits = 2
    df.minimumFractionDigits = 0
    df.isGroupingUsed = false
    return df.format(bd)
}

fun Date.formatted(pattern: String): String {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(this)
}

fun Date.formatted(): String {
    val format = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault())
    return format.format(this)
}

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun Calendar.year(): Int {
    return this.get(Calendar.YEAR)
}

fun Calendar.month(): Int {
    return this.get(Calendar.MONTH)
}

fun Calendar.date(): Int {
    return this.get(Calendar.DATE)
}

fun Calendar.hourOfDay(): Int {
    return this.get(Calendar.HOUR_OF_DAY)
}

fun Calendar.minute(): Int {
    return this.get(Calendar.MINUTE)
}

fun Calendar.toDate(useTimeZone: TimeZone = TimeZone.getDefault()): Date {
    return time.apply { timeZone = useTimeZone }
}