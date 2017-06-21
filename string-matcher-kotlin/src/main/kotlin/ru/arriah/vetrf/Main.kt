package ru.arriah.vetrf

/**
 * Created by 123 on 21.06.2017.
 */
fun main(args: Array<String>) {
    println("The very first stub")
}

fun minInt(vararg values:Int) = values.min() ?: 0

sealed class MatchResponse
/**
 * Начало и конец выводятся включительно, и начиная с единицы, а не с нуля, т.к.
 */
data class ExactMatch(val start:  Int, val end: Int): MatchResponse()
data class ApproximateMatch(val start: Int, val end: Int, val distance:Int,
                            val text: String, val pattern: String): MatchResponse()

/**
 * Данный класс используется как промежуточные, и не используется для вывода напрямую пользователю
 * А посему и не должен быть одним из вариантов ответа
 */
data class QuickApproximateMatch(val end:Int, val distance:Int)

fun analyze(pattern: String, text: String) = analyzeStripped(clearFromNoise(pattern), clearFromNoise(text))
fun analyzeStripped(pattern: String, text: String) : MatchResponse {
    println("First pass - just the distance")
    checkNonEmpty(pattern, "pattern")
    checkNonEmpty(text, "text")
    checkPatternIsNotLongerThanText(pattern, text)

    val pos = text.indexOf(pattern)
    if (pos >= 0) return ExactMatch(pos+1, pos+pattern.length)

    val quickMatch = analyzeQuick(pattern, text)
    return analyzeDetailed(pattern, text, quickMatch)
}

fun analyzeDetailed(pattern: String, text: String, quickMatch: QuickApproximateMatch): ApproximateMatch = error("Not implemented yet")


fun analyzeQuick(pattern: String, text: String): QuickApproximateMatch {
    val (n, m) = Pair(pattern.length, text.length)
    // Да, это матрица, но нам всегда нужны только ее последние две строки, а не вся она целиком
    var matrix = Pair(IntArray(m+1), IntArray(m+1))
    val step = maxOf(1, n/100)

    for (x in 1..n) {
        val patternSymbol = pattern[x-1] // Не забываем про смещение
        matrix.second[0] = x // Получить Получить непустой образец из пустой строки можно только за x - извиняйте
        for (y in 1..m) {
            val textSymbol = text[y-1] // Не забываем про смещение
            // sm.matr[1][y] = min(1+sm.matr[1][y-1], 1 + sm.matr[0][y],
            // cost(patternSymbol,textSymbol) + sm.matr[0][y-1]);
            matrix.second[y] = minInt(1+matrix.second[y-1], 1 + matrix.first[y],
                    matrix.first[y-1] + cost(patternSymbol, textSymbol))
        }
        matrix = Pair(matrix.second, IntArray(m+1))
        if (x%step == 0) println("Step $x/$n")
    }

    val (distance, end) = matrix.first.zip(0..m).minBy{it.first} ?: Pair(n, 0) // Крайне маловероятно, но все же
    return QuickApproximateMatch(end = end, distance = distance)
}
