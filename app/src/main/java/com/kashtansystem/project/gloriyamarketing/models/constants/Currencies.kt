package com.kashtansystem.project.gloriyamarketing.models.constants

enum class Currencies(val currency: String, val code: Int) {
    SUM("SUM", 1),
    USD("USD", 2);

    companion object {
        fun getCurrenciesList(): Array<String> {
            return values().map { it.currency }.toTypedArray()
        }

        fun getCodeForPosition(position: Int): Int {
            return values()[position].code
        }
    }
}