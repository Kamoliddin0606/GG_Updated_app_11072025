package com.kashtansystem.project.gloriyamarketing.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView

class StyleTextUtils{
    companion object {
        fun colorSubSeq(text: String, whichWordColor: String, colorCode: Int, textView: TextView) {
            val textUpper = text.toUpperCase()
            val whichWordColorUpper = whichWordColor.toUpperCase()
            val ss = SpannableString(text)
            var strar = 0

            while (textUpper.indexOf(whichWordColorUpper, strar) >= 0 && whichWordColor.isNotEmpty()) {
                ss.setSpan(BackgroundColorSpan(colorCode), textUpper.indexOf(whichWordColorUpper, strar), textUpper.indexOf(whichWordColorUpper, strar) + whichWordColorUpper.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                strar = textUpper.indexOf(whichWordColorUpper, strar) + whichWordColorUpper.length
            }
            textView.text = ss
        }

        fun boldSubSeq(text: String, whichWordColor: String, textView: TextView) {
            val textUpper = text.toUpperCase()
            val whichWordColorUpper = whichWordColor.toUpperCase()
            val ss = SpannableString(text)
            var strar = 0

            while (textUpper.indexOf(whichWordColorUpper, strar) >= 0 && whichWordColor.isNotEmpty()) {
                ss.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), textUpper.indexOf(whichWordColorUpper, strar), textUpper.indexOf(whichWordColorUpper, strar) + whichWordColorUpper.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                strar = textUpper.indexOf(whichWordColorUpper, strar) + whichWordColorUpper.length
            }
            textView.text = ss
        }

        fun colorSubSeqUnderLine(text: String, whichWordColor: String, colorCode: Int, textView: TextView) {
            val textUpper = text.toUpperCase()
            val whichWordColorUpper = whichWordColor.toUpperCase()
            val ss = SpannableString(text)
            ss.setSpan(UnderlineSpan(), 0, ss.length, 0)
            var strar = 0

            while (textUpper.indexOf(whichWordColorUpper, strar) >= 0 && whichWordColor.isNotEmpty()) {
                ss.setSpan(ForegroundColorSpan(colorCode), textUpper.indexOf(whichWordColorUpper, strar), textUpper.indexOf(whichWordColorUpper, strar) + whichWordColorUpper.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                strar = textUpper.indexOf(whichWordColorUpper, strar) + whichWordColorUpper.length
            }
            textView.text = ss
        }

        fun setUnderlineText(textView: TextView, text: String) {
            val content = SpannableString(text)
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            textView.text = content
        }
    }

}