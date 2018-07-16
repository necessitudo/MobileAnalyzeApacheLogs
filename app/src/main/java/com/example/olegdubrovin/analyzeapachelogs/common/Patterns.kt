package com.example.olegdubrovin.analyzeapachelogs.common

enum class Patterns(val expression: String) {
    DATE("[0-9]?[0-9]?\\/[A-z]{1}[a-z]+\\/[0-9]{4}"),
    YEAR("[0-9]{4}"),
    MONTH("[A-z]{1}[a-z]+"),
    DAY("[0-9]?[0-9]?")
}