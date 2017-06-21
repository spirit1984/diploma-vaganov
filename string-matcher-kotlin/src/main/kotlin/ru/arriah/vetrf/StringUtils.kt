package ru.arriah.vetrf

/**
 * Created by 123 on 21.06.2017.
 */
fun cost(a: Char, b: Char) = if (a==b) 0 else 1
fun checkNonEmpty(value: String, label: String = "default") {
    if (value.trim().isEmpty()) {
        error("Argument '$label' cannot be empty")
    }
}

fun clearFromNoise(text:String) = text.toUpperCase().filter{ isTGAC(it)}

fun isTGAC(ch: Char)=  ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T'
fun checkPatternIsNotLongerThanText(pattern: String, text: String) {
    if (pattern.length > text.length) {
        error("Pattern has ${pattern.length} symbols, more than text that has ${text.length} symbols")
    }
}